package lb.library;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class StringArrayAlphabetIndexer extends SectionedSectionIndexer
  {
  /**
   * @param items                   each of the items. Note that they must be sorted in a way that each chunk will belong to
   *                                a specific header. For example, chunk with anything that starts with "A"/"a", then a chunk
   *                                that all of its items start with "B"/"b" , etc...
   * @param useOnlyUppercaseHeaders whether the header will be in uppercase or not.
   *                                if true, you must order the items so that each chunk will have its items start with either the lowercase or uppercase letter
   */
  public StringArrayAlphabetIndexer(String[] items,boolean useOnlyUppercaseHeaders)
    {
    super(createSectionsFromStrings(items,useOnlyUppercaseHeaders));
    }

  private static SimpleSection[] createSectionsFromStrings(String[] items,boolean useOnlyUppercaseHeaders)
    {
    //get all of the headers of the sections and their sections-items:
    Map<String,ArrayList<String>> headerToSectionItemsMap=new HashMap<String,ArrayList<String>>();
    Set<String> alphabetSet=new TreeSet<String>();
    for(String item : items)
      {
      String firstLetter=TextUtils.isEmpty(item)?" ":useOnlyUppercaseHeaders?item.substring(0,1).toUpperCase(Locale.getDefault()):
          item.substring(0,1);
      ArrayList<String> sectionItems=headerToSectionItemsMap.get(firstLetter);
      if(sectionItems==null)
        headerToSectionItemsMap.put(firstLetter,sectionItems=new ArrayList<String>());
      sectionItems.add(item);
      alphabetSet.add(firstLetter);
      }
    //prepare the sections, and also sort each section's items :
    SimpleSection[] sections=new SimpleSection[alphabetSet.size()];
    int i=0;
    for(String headerTitle : alphabetSet)
      {
      ArrayList<String> sectionItems=headerToSectionItemsMap.get(headerTitle);
      SimpleSection simpleSection=new AlphaBetSection(sectionItems);
      simpleSection.setName(headerTitle);
      sections[i++]=simpleSection;
      }
    return sections;
    }

  public static class AlphaBetSection extends SimpleSection
    {
    private ArrayList<String> items;

    private AlphaBetSection(ArrayList<String> items)
      {
      this.items=items;
      }

    @Override
    public int getItemsCount()
      {
      return items.size();
      }

    @Override
    public String getItem(int posInSection)
      {
      return items.get(posInSection);
      }

    }


  }
