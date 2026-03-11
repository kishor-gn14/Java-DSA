import java.util.*;

public class ContainsDuplicate {
    public boolean containsDuplicate(int[] nums){
        HashSet<Integer> seen = new HashSet<>();

        for (int num: nums){
            if (!seen.add(num)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = {1,2,3,1};

        ContainsDuplicate obj = new ContainsDuplicate();
        boolean result = obj.containsDuplicate(nums);
        
        System.out.println(result);
    }
}
