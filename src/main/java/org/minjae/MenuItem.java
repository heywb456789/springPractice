package org.minjae;

import java.util.Objects;

/**
 * packageName       : org.minjae
 * fileName         : MenuItem
 * author           : MinjaeKim
 * date             : 2024-07-05
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-05        MinjaeKim       최초 생성
 */
public class MenuItem {

    private final String name;
    private final int price ;

    public MenuItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return price == menuItem.price && Objects.equals(name, menuItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }

    public boolean matches(String name) {
        return this.name.equals(name);
    }
}
