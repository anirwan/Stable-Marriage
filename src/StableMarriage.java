import java.util.*;


/**
 * Created by Anirwan on 26/01/2016.
 */


public class StableMarriage {


    private List<String> men = new LinkedList<String>();
    private List<String> women = new LinkedList<String>();

    private Map<String, List<String>> menPriority = new HashMap<String, List<String>>();
    private Map<String, List<String>> womenPriority = new HashMap<String, List<String>>();


    public StableMarriage(int size) {

        //Initialize men and women lists
        for (int i = 1; i <= size; i++) {
            String man = ("M" + i);
            String woman = ("W" + i);

            men.add(man);
            women.add(woman);
        }


        //Randomize preferences for each man
        for (String man : men) {
            menPriority.put(man, randomPreference(size, "W"));
        }

        //Randomize preferences for each woman
        for (String woman : women) {
            womenPriority.put(woman, randomPreference(size, "M"));
        }
    }


    public List<String> randomPreference(int size, String prefix) {

        List<String> manPrefers = new LinkedList<String>();
        Random rand = new Random();

        //Initial population
        for (int i = 1; i <= size; i++) {
            manPrefers.add(prefix + i);
        }

        //Randomize based on the initial population
        for (int i = size - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            String temp = manPrefers.get(i);
            manPrefers.set(i, manPrefers.get(j));
            manPrefers.set(j, temp);
        }

        return manPrefers;
    }


    public Map<String, String> match() {

        /*
        Gale-Shapley Algorithm
        1. Each single man proposes to the women he prefers most
        2. Each women replies "maybe" to the suitor she prefers most
        3. Each engaged man proposes to any woman he has not yet proposed to (regardless of status)
        4. Woman "trades-up" if she prefers the guy more
         */

        Map<String, String> engaged = new TreeMap<String, String>();
        List<String> singleMen = new ArrayList<String>();

        singleMen.addAll(men);

        while (!singleMen.isEmpty()) {
            String man = singleMen.remove(0);
            List<String> manPrefers = menPriority.get(man);

            for (String woman : manPrefers) {
                if (engaged.get(woman) == null) {
                    engaged.put(woman, man);
                    break;
                } else {
                    String otherMan = engaged.get(woman);
                    List<String> womanPrefers = womenPriority.get(woman);

                    if (womanPrefers.indexOf(man) < womanPrefers.indexOf(otherMan)) {
                        engaged.put(woman, man);
                        singleMen.add(otherMan);
                        break;
                    }
                }
            }
        }
        return engaged;
    }


    public boolean check(Map<String, String> matches) {

        Map<String, String> invertedMatches = new TreeMap<String, String>();
        for (Map.Entry<String, String> match : matches.entrySet()) {
            invertedMatches.put(match.getValue(), match.getKey());
        }

        if (!matches.values().containsAll(men)) {
            System.out.println("\nMarriages are unstable!");
            return false;
        }
        if (!matches.keySet().containsAll(women)) {
            System.out.println("\nMarriages are unstable!");
            return false;
        }

        for (Map.Entry<String, String> match : matches.entrySet()) {
            List<String> womanPrefers = womenPriority.get(match.getKey());
            List<String> womanLikesBetter = new LinkedList<String>();
            womanLikesBetter.addAll(womanPrefers.subList(0, womanPrefers.indexOf(match.getValue())));

            List<String> manPrefers = menPriority.get(match.getValue());
            List<String> manLikesBetter = new LinkedList<String>();
            manLikesBetter.addAll(manPrefers.subList(0, manPrefers.indexOf(match.getKey())));

            for (String man : womanLikesBetter) {
                String fiance = invertedMatches.get(man);
                List<String> fiancePrefers = menPriority.get(man);

                if (fiancePrefers.indexOf(fiance) > fiancePrefers.indexOf(match.getKey())) {
                    System.out.println(match.getKey() + " likes " + man + " better than " + match.getValue()
                            + " and " + man + " likes " + match.getKey() + " better than their current partner");
                    System.out.println("\nMarriages are unstable!");
                    return false;
                }
            }

            for (String woman : manLikesBetter) {
                String fiance = matches.get(woman);
                List<String> fiancePrefers = womenPriority.get(woman);

                if (fiancePrefers.indexOf(fiance) > fiancePrefers.indexOf(match.getValue())) {
                    System.out.println(match.getValue() + " likes " + woman + " better than " + match.getKey()
                            + " and " + woman + " likes " + match.getValue() + " better than their current partner");
                    System.out.println("\nMarriages are unstable!");
                    return false;
                }
            }
        }

        System.out.println("\nMarriages are stable!");
        return true;

    }


    public static void main(String[] args) {

        int input;
        Scanner scan = new Scanner(System.in);

        System.out.print("Please specify the number of men / women: ");
        input = scan.nextInt();

        StableMarriage sm = new StableMarriage(input);

        System.out.println("Men: " + sm.men);
        System.out.println("Women: " + sm.women);

        System.out.println("\nPreference map:");
        System.out.println(sm.menPriority);
        System.out.println(sm.womenPriority);
        System.out.println("\n");

        Map<String, String> matches = sm.match();

        System.out.println("Initial matches:");
        for (Map.Entry<String, String> match : matches.entrySet()) {
            System.out.println(match.getKey() + " is engaged to " + match.getValue());
        }

        sm.check(matches);

//        //Switch to verify sm.check() works
//        String temp = matches.get(sm.women.get(0));
//        matches.put(sm.women.get(0), matches.get(sm.women.get(1)));
//        matches.put(sm.women.get(1), temp);
//        System.out.println("\n" + sm.women.get(0) + " and " + sm.women.get(1) + " have been switched!");
//        sm.check(matches);


    }

}
