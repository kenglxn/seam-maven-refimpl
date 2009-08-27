package net.glxn.webcommerce.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table
public class User implements Serializable {

    private static final long serialVersionUID = -7294196286809711838L;

    private Long id;
    private Integer version;
    private String username;
    private String password;
    private String name;
    private RoleType roleType;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    private void setVersion(Integer version) {
        this.version = version;
    }

    @NotNull
    @Length(min = 3, max = 15)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    @Length(min = 5, max = 15)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User(" + username + ")";
    }

    private Collection<Order> orders;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }

    @Enumerated(EnumType.STRING)
    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
