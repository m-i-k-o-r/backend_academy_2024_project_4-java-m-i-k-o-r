package backend.academy.util;

import backend.academy.transformation.Transformation;
import backend.academy.transformation.wrapper.Wrapper;
import java.util.List;
import java.util.Random;

public class WrapperUtils {
    public static <T extends Transformation> Wrapper<T> choose(
        Random random,
        List<Wrapper<T>> wrappers
    ) {
        double randomValue = random.nextDouble();
        double cumulativeWeight = 0.0;

        for (Wrapper<T> wrapper : wrappers) {
            cumulativeWeight += wrapper.weight();
            if (randomValue <= cumulativeWeight) {
                return wrapper;
            }
        }

        return wrappers.getLast();
    }

    public static <T extends Transformation> List<Wrapper<T>> normalize(
        List<Wrapper<T>> wrappers
    ) {
        double totalWeight = wrappers.stream()
            .mapToDouble(Wrapper::weight)
            .sum();

        if (totalWeight == 0) {
            throw new IllegalArgumentException(" ! Сумма весов не может ровняться 0");
        }

        return wrappers.stream()
            .peek(wt -> wt.weight(wt.weight() / totalWeight))
            .toList();
    }
}
