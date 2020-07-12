package com.hao.core.pojo.entity;

import com.hao.core.pojo.specification.Specification;
import com.hao.core.pojo.specification.SpecificationOption;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;

import java.io.Serializable;
import java.util.List;

public class SpecEntity implements Serializable {
    //规格对象
    private Specification spec;
    //规格选项集合
    private List<SpecificationOption> specOption;

    public Specification getSpec() {
        return spec;
    }

    public void setSpec(Specification spec) {
        this.spec = spec;
    }

    public List<SpecificationOption> getSpecOption() {
        return specOption;
    }

    public void setSpecOption(List<SpecificationOption> specOption) {
        this.specOption = specOption;
    }

    @Override
    public String toString() {
        return "SpecEntity{" +
                "spec=" + spec +
                ", specOption=" + specOption +
                '}';
    }
}

