/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.gumga.maven.plugins.gumgag;

import gumga.framework.domain.domains.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author munif
 */
@Mojo(name = "api", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class GeraAPI extends AbstractMojo {

    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Entidade
     */
    @Parameter(property = "entidade", defaultValue = "all")
    private String nomeCompletoEntidade;
    private String nomePacoteBase;
    private String nomeEntidade;
    private String nomePacoteApi;
    private String nomePacoteWeb;
    private String pastaApi;
    private String pastaWeb;
    private String pastaJSP;

    private Class classeEntidade;
    private String pastaScripts;
    private String pastaResources;

    private List<Class> dependenciasManyToOne;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {
            nomePacoteBase = nomeCompletoEntidade.substring(0, nomeCompletoEntidade.lastIndexOf(".domain"));
            nomeEntidade = nomeCompletoEntidade.substring(nomeCompletoEntidade.lastIndexOf('.') + 1);

            nomePacoteApi = nomePacoteBase + ".presentation.api";
            nomePacoteWeb = nomePacoteBase + ".presentation.web";

            pastaApi = project.getCompileSourceRoots().get(0) + "/".concat(nomePacoteApi.replaceAll("\\.", "/"));

            getLog().info("Iniciando plugin Gerador de API ");
            getLog().info("Gerando para " + nomeEntidade);

            classeEntidade = Util.getClassLoader(project).loadClass(nomeCompletoEntidade);

            geraApi();
        } catch (Exception ex) {
            getLog().error(ex);
        }

    }

    private void geraApi() {
        File f = new File(pastaApi);
        f.mkdirs();
        File arquivoClasse = new File(pastaApi + "/" + nomeEntidade + "API.java");
        try {
            FileWriter fw = new FileWriter(arquivoClasse);
            fw.write(""
                    + "package " + nomePacoteApi + ";\n"
                    + "\n"
                    + "import  " + nomePacoteBase + ".application.service." + nomeEntidade + "Service;\n"
                    + "import " + nomeCompletoEntidade + ";\n"
                    + "import gumga.framework.application.GumgaService;\n"
                    + "import gumga.framework.presentation.GumgaAPI;\n"
                    + "\n"
                    + "import org.springframework.beans.factory.annotation.Autowired;\n"
                    + "import org.springframework.web.bind.annotation.RequestMapping;\n"
                    + "import org.springframework.web.bind.annotation.RestController;\n"
                    + "import org.springframework.web.bind.annotation.PathVariable;\n"
                    + "\n"
                    + "@RestController\n"
                    + "@RequestMapping(\"/api/" + nomeEntidade.toLowerCase() + "\")\n"
                    + "public class " + nomeEntidade + "API extends GumgaAPI<" + nomeEntidade + ", Long> {\n"
                    + "\n"
                    + "    @Autowired\n"
                    + "    public " + nomeEntidade + "API(GumgaService<" + nomeEntidade + ", Long> service) {\n"
                    + "        super(service);\n"
                    + "    }\n\n");

            sobrecarregaLoad(fw);

            fw.write(""
                    + "\n"
                    + "}"
                    + "\n");

            fw.close();
        } catch (Exception ex) {
            getLog().error(ex);
        }
    }

    private void sobrecarregaLoad(FileWriter fw) throws IOException {
        List<Field> todosAtributos = Util.getTodosAtributos(classeEntidade);
        for (Field f : todosAtributos) {
            if (f.isAnnotationPresent(OneToMany.class) || f.isAnnotationPresent(ManyToMany.class)) {
                fw.write(""
                        + "    @Override\n"
                        + "    public " + nomeEntidade + " load(@PathVariable Long id) {\n"
                        + "        return ((" + nomeEntidade + "Service)service).load" + nomeEntidade + "Fat(id);\n"
                        + "    }"
                        + "");
                return;
            }
        }

    }


}