package PracticeSet.atlaslearnings.day12;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Task11 {
    public static void main(String[] args) {
        Hashtable<String, Integer> ht = new Hashtable<>();
        ht.put("A", 101);
        ht.put("K", 102);
        ht.put("M", 103);
        for (Map.Entry<String, Integer> e: ht.entrySet()){
            System.out.println(e.getKey()+" "+e.getValue());
        }
        System.out.println(ht.get("A"));
        System.out.println(ht.get("K"));
        System.out.println(ht.get("M"));


        HashMap<String, Integer> ht1 = new HashMap<>();
        ht1.put("A", 101);
        ht1.put("K", 102);
        ht1.put("M", 103);
        for (Map.Entry<String, Integer> e: ht1.entrySet()){
            System.out.println(e.getKey()+" "+e.getValue());
        }

        ht1.forEach((x,y)->System.out.println(x+" "+y));

    }
}
