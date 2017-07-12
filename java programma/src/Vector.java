public class Vector {

    public float x;
    public float y;
    public float z;

    public int ix;
    public int iy;
    public int iz;

    //2d float
    public Vector(float x, float y){
        this.x = x;
        this.y = y;
        this.z = 0;
        intify();
    }

    //2d int
    public Vector(int x, int y){
        this.x = x;
        this.y = y;
        this.z = 0;
        intify();
    }

    //3d float
    public Vector(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        intify();
    }

    //3d int
    public Vector(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        intify();
    }

    //copy constructor
    public Vector(Vector o){
        this.x = o.x;
        this.y = o.y;
        this.z = o.z;
        intify();
    }

    //set values
    public void setX(float x){
        this.x = x;
        intify();
    }

    public void setY(float y){
        this.y = y;
        intify();
    }

    public void setZ(float z){
        this.z = z;
        intify();
    }

    public void setX(int x){
        this.x = (float)x;
        intify();
    }

    public void setY(int y){
        this.y = (float)y;
        intify();
    }

    public void setZ(int z){
        this.z = (float)z;
        intify();
    }

    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        intify();
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
        intify();
    }

    public void set(int x, int y, int z){
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        intify();
    }

    public void set(int x, int y){
        this.x = (float)x;
        this.y = (float)y;
        intify();
    }


    //basic operators

    //a += b;
    //a.add(b);
    public void add(Vector o){
        this.x += o.x;
        this.y += o.y;
        this.z += o.z;
        intify();
    }

    //sum = a + b;
    //sum = Vector.add(a, b);
    public static Vector add(Vector a, Vector b){
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    //a -= b;
    //a.sub(b);
    public void sub(Vector o){
        this.x -= o.x;
        this.y -= o.y;
        this.z -= o.z;
        intify();
    }

    //dif = a - b;
    //dif = Vector.sub(a, b);
    public static Vector sub(Vector a, Vector b){
        return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    //v *= s;
    //v.mult(s);
    public void mult(float s){
        this.x *= s;
        this.y *= s;
        this.z *= s;
        intify();
    }

    //prd = v * s;
    //prd = Vector.mult(v, s);
    public static Vector mult(Vector v, float s){
        return new Vector( v.x * s, v.y * s, v.z * s );
    }

    //int
    public void mult(int s){
        this.x *= (float)s;
        this.y *= (float)s;
        this.z *= (float)s;
        intify();
    }

    public static Vector mult(Vector v, int s){
        return new Vector( v.x * (float)s, v.y * (float)s, v.z * (float)s );
    }

    //v /= s;
    //v.div(s);
    public void div(float s){
        this.x /= s;
        this.y /= s;
        this.z /= s;
        intify();
    }

    //quo = v * s;
    //quo = Vector.div(v, s);
    public static Vector div(Vector v, float s){
        return new Vector( v.x / s, v.y / s, v.z / s );
    }

    //int
    public void div(int s){
        this.x /= (float)s;
        this.y /= (float)s;
        this.z /= (float)s;
        intify();
    }


    public static Vector div(Vector v, int s){
        return new Vector( v.x / (float)s, v.y / (float)s, v.z / (float)s );
    }

    //Vector functions

    //squared magnitude
    public float sqmag(){
        return (float)(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    //magnitude
    public float mag(){
        return (float)Math.sqrt(sqmag());
    }

    public void normalize(){
        this.div(this.mag());
    }

    public static Vector normalize(Vector v){
        return Vector.div(v, v.mag());
    }

    public void setMag(float m){
        this.normalize();
        this.mult(m);
    }

    public static Vector setMag(Vector v, float m){
        return mult(normalize(v), m);
    }

    public void setMag(int m){
        this.normalize();
        this.mult(m);
    }

    public static Vector setMag(Vector v, int m){
        return mult(normalize(v), m);
    }

    public static Vector rand3D(){
        return normalize(new Vector((float)Math.random(), (float)Math.random(), (float)Math.random()));
    }

    public static Vector rand2D(){
        return normalize(new Vector((float)Math.random(), (float)Math.random(), 0f));
    }

    public static Vector unit2D(float rad){
        return new Vector((float)Math.cos(rad), (float)Math.sin(rad));
    }

    public float distance(Vector b){
        return (float)Math.sqrt( Math.pow(b.x - this.x, 2) + Math.pow(b.y - this.y, 2) + Math.pow(b.z - this.z, 2));
    }

    public static float distance(Vector a, Vector b){
        return (float)Math.sqrt( Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2) + Math.pow(b.z - a.z, 2));
    }

    public float dot(Vector b){
        return dot(this, b);
    }

    public static float dot(Vector a, Vector b){
        return (a.x * b.x) + (a.y * b.y) + (a.z * b.z);
    }

    public Vector cross(Vector b){
        return cross(this, b);
    }

    public static Vector cross(Vector a, Vector b){
        return new Vector(
                (a.y * b.z) - (a.z * b.y),
                (a.z * b.x) - (a.x * b.z),
                (a.x * b.y) - (a.y * b.x)
        );
    }

    public void rotate(float rad){
        this.x = (float)(x * Math.cos(rad) - y * Math.sin(rad));
        this.y = (float)(x * Math.sin(rad) + y * Math.cos(rad));
        intify();
    }

    public static Vector rotate(Vector v, float rad){
        return new Vector(
                (float)(v.x * Math.cos(rad) - v.y * Math.sin(rad)),
                (float)(v.x * Math.sin(rad) + v.y * Math.cos(rad)),
                v.z
        );
    }

    public float dir(){
        return (float)Math.atan2(y, x);
    }

    public static Vector lerp(Vector a, Vector b, float c){
        return add( mult(a, c), mult(b, 1.0f - c));
    }

    public void intify(){
        ix = (int)x;
        iy = (int)y;
        iz = (int)z;
    }
}