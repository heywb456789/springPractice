package org.minjae;

import java.util.Objects;

/**
 * packageName       : org.minjae
 * fileName         : Cook
 * author           : MinjaeKim
 * date             : 2024-07-05
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-05        MinjaeKim       최초 생성
 */
public class Cook {

    private final String name;
    private final int price;

    public Cook(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Cook(MenuItem menuItem) {
        this.name = menuItem.getName();
        this.price = menuItem.getPrice();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cook cook = (Cook) o;
        return price == cook.price && Objects.equals(name, cook.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
