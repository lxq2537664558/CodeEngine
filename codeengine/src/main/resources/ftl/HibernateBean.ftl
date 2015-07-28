package ${package};

//自动生成,请勿修改
<#list properties as pro>
<#if pro.proType == "Date"> 
import java.util.Date;  
<#break>
</#if>
</#list>

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="${className}")
public class <@upperFC>${className}</@upperFC>Dto extends BaseDto{
    private static final long serialVersionUID = 1L;

<#list properties as pro>
	@Column
	private ${pro.proType} ${pro.proName};	<#if pro.description !="">		//${pro.description}<#else>	</#if>
</#list>
	
<#list properties as pro>
	<#if pro.description !="">/** ${pro.description} */</#if>
	public void set<@upperFC>${pro.proName}</@upperFC>(${pro.proType} ${pro.proName}){
		this.${pro.proName}=${pro.proName};
	}
	
	<#if pro.description !="">/** ${pro.description} */</#if>
	public ${pro.proType} get<@upperFC>${pro.proName}</@upperFC>(){
		return this.${pro.proName};
	}
	
</#list>
	@Override
	public String toString() {
		return "${className} ["
		<#list properties as pro>
		+" ${pro.proName}="+${pro.proName}
		</#list>
		+ "]";
	}

}