package phr.lib;

import java.io.Serializable;

/**
 * Created by Anupam on 10-Apr-18.
 *
 * This is the record permission object.
 *
 * It contains all information that is found in its corosponding db table, however the record name and health professional name are corolated using the where and "and" conditions in the sql query.
 *
 */

public class RecordPermission implements Serializable {
    private int r_id, hp_id, id;
    private String r_name, hp_name;

    public RecordPermission (int health_professional_id, String health_professional_name, int record_id,  String record_name, int permission_id){
        this.hp_id = health_professional_id;
        this.hp_name = health_professional_name;
        this.r_id = record_id;
        this.r_name = record_name;
        this.id = permission_id;
    }
    public RecordPermission (int health_professional_id, String health_professional_name, int record_id,  String record_name){
        this.hp_id = health_professional_id;
        this.hp_name = health_professional_name;
        this.r_id = record_id;
        this.r_name = record_name;
    }

    public int getR_id() {
        return r_id;
    }

    public void setR_id(int r_id) {
        this.r_id = r_id;
    }

    public int getHp_id() {
        return hp_id;
    }

    public void setHp_id(int hp_id) {
        this.hp_id = hp_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public String getHp_name() {
        return hp_name;
    }

    public void setHp_name(String hp_name) {
        this.hp_name = hp_name;
    }

    @Override
    public String toString() {
        return "RecordPermission{" +
                "r_id=" + r_id +
                ", hp_id=" + hp_id +
                ", id=" + id +
                ", r_name='" + r_name + '\'' +
                ", hp_name='" + hp_name + '\'' +
                '}';
    }
}
