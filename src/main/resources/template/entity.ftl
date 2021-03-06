package ${package};
import io.gumga.domain.GumgaModel; //TODO RETIRAR OS IMPORTS DESNECESSÁRIOS
import io.gumga.domain.GumgaModelUUID;
import io.gumga.domain.GumgaMultitenancy;
import java.io.Serializable;
import java.util.*;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;
import io.gumga.domain.domains.*;
import org.hibernate.envers.Audited;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Columns;

@GumgaMultitenancy
@Audited
<#if "YES" == "${isExtends}">
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name="${entityName}TYPE")
</#if>
@Entity(name = "${entityName}")
@Table(name = "${entityName}", indexes = {
    @Index(name = "${entityName}_gum_oi", columnList = "oi")
})
<#if "GumgaModel<Long>" == "${superClass}">
@SequenceGenerator(name = GumgaModel.SEQ_NAME, sequenceName = "SEQ_${entityName}")
<#elseif "GumgaModelUUID" != "${superClass}">
@DiscriminatorValue("${entityName}")
</#if>
public class ${entityName} extends ${superClass} {

<#if "GumgaModel<Long>" == "${superClass}">
    @Version
    @Column(name = "version")
    private Integer version;
</#if>
<#include "attributes.ftl">

    public ${entityName}() {}
<#include "generatorGettersAndSetters.ftl">
}
