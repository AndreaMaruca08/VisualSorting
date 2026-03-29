package utilities;

public enum CreationStrategy {
    INCREMENTAL{
        @Override
        public void fill(long[] arr, long min, long max) {
            for(int i = 0; i < arr.length; i++)
                arr[i] = min + i* 2L;
            ArraysFactory.shuffle(arr);
        }
    },
    RANDOM{
        @Override
        public void fill(long[] arr, long min, long max) {
            for(int i = 0; i < arr.length; i++)
                arr[i] = (long) (Math.random() * (max-min+1) + min);
        }
    };

    public void fill(long[] arr, long min, long max) {throw new UnsupportedOperationException();}

}
