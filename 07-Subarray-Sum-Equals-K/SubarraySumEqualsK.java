import java.util.*;

public class SubarraySumEqualsK {
    public int subArraySum(int[] nums, int k) {
        HashMap<Integer, Integer> prefixCounts = new HashMap<>();
        prefixCounts.put(0, 1);

        int prefixSum = 0;
        int count = 0;

        for (int num : nums) {
            prefixSum += num;
            int complement = prefixSum - k;
            count += prefixCounts.getOrDefault(complement, 0);
            prefixCounts.put(prefixSum, prefixCounts.getOrDefault(prefixSum, 0) + 1);
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {1,1,1};
        int k = 2;
        
        SubarraySumEqualsK obj = new SubarraySumEqualsK();
        int result = obj.subArraySum(nums, k);
        System.out.println(result);
    }
}