package configgen.value;

import configgen.Node;
import configgen.define.Range;
import configgen.type.TString;

import java.util.List;

public class VString extends VPrimitive {
    public final TString tstring;
    public final String value;

    public VString(Node parent, String name, TString type, List<Cell> data) {
        super(parent, name, type, data);
        tstring = type;

        if (tstring.subtype == TString.Subtype.STRING){
            value = raw.data;
        }else{
            I18n i18n = ((VDb)root).i18n;
            value = i18n.get(raw.data);
        }
    }

    @Override
    public boolean checkRange(Range range) {
        int len = value.length();
        return len >= range.min && len <= range.max;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof VString && value.equals(((VString) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visit(this);
    }
}
