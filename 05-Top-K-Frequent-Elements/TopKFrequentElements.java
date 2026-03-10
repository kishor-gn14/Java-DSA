import java.util.*;

public class TopKFrequentElements {
    public int[] topKFrequentElements(int[] nums, int k) {
        HashMap<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>((a,b) -> freqMap.get(a) - freqMap.get(b));

        for (int num : freqMap.keySet()) {
            minHeap.offer(num);

            if(minHeap.size()>k) {
                minHeap.poll();
            }
        }

        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll();
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1,1,1,2,2,3};
        int k = 2;

        TopKFrequentElements obj = new TopKFrequentElements();
        int[] result = obj.topKFrequentElements(nums, k);
        System.out.println(Arrays.toString(result));
    }
}