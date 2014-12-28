/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lb.library;

import android.text.TextUtils;
import android.util.SparseIntArray;
import android.widget.SectionIndexer;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A {@link android.widget.SectionIndexer} implementation on an array of {@link String} objects. Based on the {@link android.widget.AlphabetIndexer}.
 */
public class StringArrayAlphabetIndexer implements SectionIndexer
  {

  private static final char UNCATEGORIZED_SECTION=' ';

  /**
   * The array of data
   */
  protected String[] mStringsArray;

  /**
   * The string of characters that make up the indexing sections.
   */
  protected CharSequence mAlphabet;

  /**
   * Cached length of the alphabet array.
   */
  private final int mAlphabetLength;

  /**
   * This contains a cache of the computed indices so far. It will get reset whenever the dataset changes or the
   * cursor changes.
   */
  private final SparseIntArray mAlphaMap;

  /**
   * Use a collator to compare strings in a localized manner.
   */
  private final java.text.Collator mCollator;

  /**
   * The section array converted from the alphabet string.
   */
  private final String[] mAlphabetArray;

  public StringArrayAlphabetIndexer(final String[] array)
    {
    mStringsArray=array==null?new String[0]:array;
    final HashSet<Character> alphabetSet=new HashSet<Character>();
    for(final String string : mStringsArray)
      {
      final String trimmed=string==null?"":string.trim();
      if(!TextUtils.isEmpty(trimmed))
        alphabetSet.add(Character.toUpperCase(trimmed.charAt(0)));
      else
        alphabetSet.add(' ');
      }
    final SortedSet<Character> set=new TreeSet<Character>();
    for(final Character character : alphabetSet)
      set.add(character);
    final StringBuilder sb=new StringBuilder();
    for(final Character character : set)
      sb.append(character);
    final char[] alphabetCharacters=sb.toString().toCharArray();

    mAlphabet=new String(alphabetCharacters);
    mAlphabetLength=mAlphabet.length();
    mAlphabetArray=new String[mAlphabetLength];
    for(int i=0;i<mAlphabetLength;++i)
      mAlphabetArray[i]=Character.toString(mAlphabet.charAt(i));
    mAlphaMap=new SparseIntArray(mAlphabetLength);

    // Get a Collator for the current locale for string comparisons.
    mCollator=java.text.Collator.getInstance();
    mCollator.setStrength(java.text.Collator.PRIMARY);
    }

  /**
   * Returns the section array constructed from the alphabet provided in the constructor.
   *
   * @return the section array
   */
  @Override
  public String[] getSections()
    {
    return mAlphabetArray;
    }

  /**
   * Default implementation compares the first character of word with letter.
   */
  protected int compare(final String word,final String letter)
    {
    final String firstLetter;
    if(TextUtils.isEmpty(word))
      {
      firstLetter=" ";
      }
    else
      firstLetter=word.substring(0,1);
    final int result=mCollator.compare(firstLetter,letter);
//  Log.d("AppLog","firstLetter:"+firstLetter+" letter:"+letter+" result:"+result );
    return result;
    }

  /**
   * Performs a binary search or cache lookup to find the first row that matches a given section's starting letter.
   *
   * @param sectionIndex the section to search for
   * @return the row index of the first occurrence, or the nearest next letter. For instance, if searching for "T" and
   * no "T" is found, then the first row starting with "U" or any higher letter is returned. If there is no
   * data following "T" at all, then the list size is returned.
   */
  @Override
  public int getPositionForSection(int sectionIndex)
    {
    final SparseIntArray alphaMap=mAlphaMap;
    if(mStringsArray==null||mAlphabet==null)
      return 0;
    // Check bounds
    if(sectionIndex<=0||mAlphabetLength==0)
      return 0;
    if(sectionIndex>=mAlphabetLength)
      sectionIndex=mAlphabetLength-1;
    final int count=mStringsArray.length;
    int start=0, end=count, pos;

    final char letter=mAlphabet.charAt(sectionIndex);
    final String targetLetter=Character.toString(letter);
    final int key=letter;
    // Check map
    if(Integer.MIN_VALUE!=(pos=alphaMap.get(key,Integer.MIN_VALUE)))
      {
      // Is it approximate? Using negative value to indicate that it's
      // an approximation and positive value when it is the accurate
      // position.
      if(pos<0)
        {
        pos=-pos;
        end=pos;
        }
      else
        {
        // Not approximate, this is the confirmed start of section, return it
        return pos;
        }
      }

    // Do we have the position of the previous section?
    if(sectionIndex>0)
      {
      final int prevLetter=mAlphabet.charAt(sectionIndex-1);
      final int prevLetterPos=alphaMap.get(prevLetter,Integer.MIN_VALUE);
      if(prevLetterPos!=Integer.MIN_VALUE)
        {
        start=Math.abs(prevLetterPos);
        }
      }

    // Now that we have a possibly optimized start and end, let's binary search

    pos=(end+start)/2;

    while(pos<end)
      {
      // Get letter at pos
      final String curName=mStringsArray[pos];
      if(curName==null)
        {
        if(pos==0)
          {
          break;
          }
        else
          {
          pos--;
          continue;
          }
        }
      final int diff=compare(curName,targetLetter);
      if(diff!=0)
        {
        // TODO: Commenting out approximation code because it doesn't work for certain
        // lists with custom comparators
        // Enter approximation in hash if a better solution doesn't exist
        // String startingLetter = Character.toString(getFirstLetter(curName));
        // int startingLetterKey = startingLetter.charAt(0);
        // int curPos = alphaMap.get(startingLetterKey, Integer.MIN_VALUE);
        // if (curPos == Integer.MIN_VALUE || Math.abs(curPos) > pos) {
        // Negative pos indicates that it is an approximation
        // alphaMap.put(startingLetterKey, -pos);
        // }
        // if (mCollator.compare(startingLetter, targetLetter) < 0) {
        if(diff<0)
          {
          start=pos+1;
          if(start>=count)
            {
            pos=count;
            break;
            }
          }
        else
          {
          end=pos;
          }
        }
      else
        {
        // They're the same, but that doesn't mean it's the start
        if(start==pos)
          {
          // This is it
          break;
          }
        else
          {
          // Need to go further lower to find the starting row
          end=pos;
          }
        }
      pos=(start+end)/2;
      }
    alphaMap.put(key,pos);
    return pos;
    }

  /**
   * Returns the section index for a given position in the list by querying the item and comparing it with all items
   * in the section array.
   */
  @Override
  public int getSectionForPosition(final int position)
    {
    try
      {
      if(mStringsArray==null||mStringsArray.length==0)
        return 0;
      final String curName=mStringsArray[position];
      // Linear search, as there are only a few items in the section index
      // Could speed this up later if it actually gets used.
      // TODO use binary search
      for(int i=0;i<mAlphabetLength;++i)
        {
        final char letter=mAlphabet.charAt(i);
        if(TextUtils.isEmpty(curName)&&letter==UNCATEGORIZED_SECTION)
          return i;
        final String targetLetter=Character.toString(letter);
        if(compare(curName,targetLetter)==0)
          return i;
        }
      return 0; // Don't recognize the letter - falls under zero'th section
      }
    catch(final Exception ex)
      {
      return 0;
      }
    }
  }
