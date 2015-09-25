package configgen.value;

import configgen.CSV;
import configgen.Node;
import configgen.type.TList;
import configgen.type.TMap;

import java.util.*;
import java.util.stream.Collectors;

public class VMap extends Value {
    public final TMap type;
    public final Map<Value, Value> map = new LinkedHashMap<>();

    public VMap(Node parent, String link, TMap type, List<Cell> data) {
        super(parent, link);
        this.type = type;

        Assert(data.size() == type.columnSpan());
        int kc = type.key.columnSpan();
        int vc = type.value.columnSpan();
        int idx = 0;
        for (int i = 1; i <= type.count; i++) {
            int s = (i - 1) * (kc + vc);
            if (!data.get(s).data.isEmpty()) {
                Value key = Value.create(this, "key" + idx, type.key, data.subList(s, s + kc));
                Value value = Value.create(this, "value" + idx, type.value, data.subList(s + kc, s + kc + vc));
                Assert(null == map.put(key, value), "map key duplicate, " + key);
                idx++;
            } else {
                for (Cell dc : data.subList(s, s + kc + vc)) {
                    Assert(dc.data.isEmpty(), "map entry ignore by first cell empty, but part filled, " + dc);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof VMap && type == ((VMap) o).type && map.equals(((VMap) o).map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

}
