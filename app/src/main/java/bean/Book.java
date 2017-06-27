package bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by admin on 2017-6-27.
 */
@Entity(indexes = {
        @Index(value = "name DESC", unique = true)
})
public class Book {
    @Id
    private Long id;

    @NotNull
    private String name;
    private float price;
    private String address;
@Generated(hash = 1586794087)
public Book(Long id, @NotNull String name, float price, String address) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.address = address;
}
@Generated(hash = 1839243756)
public Book() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getName() {
    return this.name;
}
public void setName(String name) {
    this.name = name;
}
public float getPrice() {
    return this.price;
}
public void setPrice(float price) {
    this.price = price;
}
public String getAddress() {
    return this.address;
}
public void setAddress(String address) {
    this.address = address;
}
}

