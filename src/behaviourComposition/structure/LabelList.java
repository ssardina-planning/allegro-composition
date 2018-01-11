package behaviourComposition.structure;

import java.util.ArrayList;

public class LabelList extends ArrayList<Label> {

    /**
     * 
     */
    private static final long serialVersionUID = -4432808082427199648L;
    
    public boolean equals(Object o) {
        if (o instanceof LabelList) {
            LabelList list = (LabelList)o;
            if (this.size() == list.size()) {
                for (int i = 0; i < this.size(); i++) {
                    if (!this.get(i).equals(list.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public int hashCode() {
        int res = 1;
        for (Label l : this) {
            res += l.hashCode()*2;
        }
        return res;
    }
    
}