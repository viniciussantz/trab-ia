import weka.classifiers.rules.Prism;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.SimpleCart;
import weka.core.Drawable;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProcessadorBase {

    private final File pastaProjeto;
    private final File pastaResultados;
    private final LeitorDados leitorDados;
    private final ExtratorRegras extratorRegras;

    public ProcessadorBase(File pastaProjeto, File pastaResultados) {
        this.pastaProjeto = pastaProjeto;
        this.pastaResultados = pastaResultados;
        this.leitorDados = new LeitorDados();
        this.extratorRegras = new ExtratorRegras();
    }

    public void processar(String id, String arquivoCsv, String classe) throws Exception {
        Instances dados = leitorDados.carregar(new File(pastaProjeto, arquivoCsv), classe);

        File pastaBase = new File(pastaResultados, id);
        if (!pastaBase.exists()) {
            pastaBase.mkdirs();
        }

        gerarC45(dados, pastaBase);
        gerarCart(dados, pastaBase);
        gerarPrism(dados, pastaBase);
    }

    private void gerarC45(Instances dados, File pastaBase) throws Exception {
        J48 c45 = new J48();
        c45.buildClassifier(dados);

        salvar(pastaBase.toPath().resolve("arvore_c45.txt"), c45.toString());
        salvar(
                pastaBase.toPath().resolve("regras_c45.txt"),
                extratorRegras.extrair(c45.toString(), dados.classAttribute().name())
        );

        if (c45 instanceof Drawable) {
            salvar(pastaBase.toPath().resolve("arvore_c45.dot"), ((Drawable) c45).graph());
        }
    }

    private void gerarCart(Instances dados, File pastaBase) throws Exception {
        SimpleCart cart = new SimpleCart();
        cart.buildClassifier(dados);

        salvar(pastaBase.toPath().resolve("arvore_cart.txt"), cart.toString());
        salvar(
                pastaBase.toPath().resolve("regras_cart.txt"),
                extratorRegras.extrair(cart.toString(), dados.classAttribute().name())
        );
    }

    private void gerarPrism(Instances dados, File pastaBase) throws Exception {
        Instances discretizado = leitorDados.discretizar(dados);
        Prism prism = new Prism();
        prism.buildClassifier(discretizado);
        salvar(pastaBase.toPath().resolve("regras_prism.txt"), prism.toString());
    }

    private void salvar(Path arquivo, String conteudo) throws IOException {
        Files.writeString(arquivo, conteudo == null ? "" : conteudo, StandardCharsets.UTF_8);
    }
}
