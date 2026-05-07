package model;

import javafx.beans.property.*;

public class Point {
    private final IntegerProperty id;
    private DoubleProperty x, y, z;

    public Point(int id, double x, double y, double z) {
        this.id = new SimpleIntegerProperty(id);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.z = new SimpleDoubleProperty(z);
    }

    public int getId() { return id.get(); }
    public double getX() { return x.get(); }
    public double getY() { return y.get(); }
    public double getZ() { return z.get(); }

    public void setX(double x){
        this.x.set(x);
    }
    public void setY(double y ){
        this.y.set(y);
    }
    public void setZ(double z){
        this.z.set(z);
    }

    public IntegerProperty idProperty() { return id; }
    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }
    public DoubleProperty zProperty() { return z; }
}
