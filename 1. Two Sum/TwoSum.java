import java.util.*;

public class TwoSum{
    public int[] twoSum(int[] nums, int target){
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int compliment = target - nums[i];

            if (map.containsKey(compliment)) {
                return new int[]{map.get(compliment), i};
            }

            map.put(nums[i], i);
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int target = 9;

        TwoSum obj = new TwoSum();
        int[] result = obj.twoSum(nums, target);
        System.out.println(result[0] + "," + result[1]);
    }
}