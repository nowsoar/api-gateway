package github.nowsoar.common.rule;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @description:
 * @author: ZKP
 * @time: 2023/11/1
 */
public class Rule implements Comparable<Rule>, Serializable {

    //全局唯一规则id
    private String id;

    //规则名称
    private String name;

    //规则对应的协议
    private String protocol;

    //规则优先级
    private Integer order;

    private Set<FilterConfig> filterConfigs = new HashSet<>();

    public Rule() {
        super();
    }

    public Rule(String id, String name, String protocol, Integer order, Set<FilterConfig> filterConfigs) {
        super();
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.order = order;
        this.filterConfigs = filterConfigs;
    }

    //配置类
    static class FilterConfig {

        private String id;

        //配置信息
        private String config;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FilterConfig that = (FilterConfig) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Override
    public int compareTo(Rule o) {
        return 0;
    }
}
