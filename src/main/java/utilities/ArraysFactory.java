package utilities;

public final class ArraysFactory {
    private ArraysFactory() {}

    public static long[] createArray(int size, long min, long max, CreationStrategy strategy) {
        if(min > max) throw new BadDataEx("min > max");
        if(min < 0) throw new BadDataEx("min < 0");
        if(max > 1_000_000_000) throw new BadDataEx("max > 1_000_000");
        if(size < 0) throw new BadDataEx("size < 0");
        if(size > 350) throw new BadDataEx("size > 350");

        long[] arr = new long[size];

        do {
            strategy.fill(arr, min, max);
        }while (isSorted(arr));

        return arr;
    }

    public static void shuffle(long[] arr) {
        for(int i = 0; i < arr.length; i++) {
            int randomIndex = (int) (Math.random() * arr.length);
            long temp = arr[i];
            arr[i] = arr[randomIndex];
            arr[randomIndex] = temp;
            if(i % 2 == 0)
                try {
                    Thread.sleep(2);
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
        }
    }

    public static long[] createArrayIncremental(int size, long min, long max) {
        return createArray(size, min, max, CreationStrategy.INCREMENTAL);
    }

    public static long[] createArrayRandom(int size, long min, long max) {
        return createArray(size, min, max, CreationStrategy.RANDOM);
    }

    public static boolean isSorted(long[] arr) {
        for(int i = 0; i < arr.length-1; i++)
            if(arr[i] > arr[i+1]) return false;
        return true;
    }

    public static long findHighest(long[] arr) {
        long max = arr[0];
        for(long val : arr)
            if(val > max) max = val;
        return max;
    }
}
