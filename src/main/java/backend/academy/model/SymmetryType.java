package backend.academy.model;

import lombok.Getter;

@Getter
public enum SymmetryType {
    NONE("Нет"),
    HORIZONTAL("Горизонтальная"),
    VERTICAL("Вертикальная"),
    BOTH("Горизонтальная и вертикальная"),
    RADIAL_2("Радиальная с 2 осями"),
    RADIAL_4("Радиальная с 4 осями");

    private final String description;

    SymmetryType(String description) {
        this.description = description;
    }

}
