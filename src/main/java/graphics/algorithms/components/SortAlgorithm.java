package graphics.algorithms.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utilities.ArraysFactory;

@Getter
@Setter
@AllArgsConstructor
public abstract class SortAlgorithm {
    protected final String name;
    protected final String description;
    protected final String complexity;

    public abstract void actualSort(long[] arr);

    protected String measureSort(long[] arr){
        double start = System.nanoTime();

        actualSort(arr);

        double end = System.nanoTime() - start;

        if(!ArraysFactory.isSorted(arr))
            throw new ArrayNotSortedException(name, "Array non ordinato");

        return """
                Algorithm: %s
                Complexity: %s
                %s
                ------
                Time nano: %f
                Time ms: %f
                Time s: %f""".formatted(name, complexity, description, end, end / 1_000_000, end / 1_000_000_000);
    }
}
