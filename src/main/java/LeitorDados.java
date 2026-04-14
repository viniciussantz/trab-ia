import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.io.File;
import java.util.Locale;

public class LeitorDados {

    public Instances carregar(File csv, String classe) throws Exception {
        File arquivoPreparado = LimpezaCabecalho.preparar(csv);

        CSVLoader loader = new CSVLoader();
        loader.setSource(arquivoPreparado);
        Instances dados = loader.getDataSet();

        StringToNominal filtroTexto = new StringToNominal();
        filtroTexto.setAttributeRange("first-last");
        filtroTexto.setInputFormat(dados);
        dados = Filter.useFilter(dados, filtroTexto);

        int indiceClasse = buscarIndiceClasse(dados, classe);
        if (indiceClasse < 0) {
            throw new IllegalStateException("Classe nao encontrada: " + classe);
        }

        dados.setClassIndex(indiceClasse);
        return dados;
    }

    public Instances discretizar(Instances dados) throws Exception {
        Discretize filtro = new Discretize();
        filtro.setInputFormat(dados);
        Instances discretizado = Filter.useFilter(dados, filtro);
        discretizado.setClassIndex(dados.classIndex());
        return discretizado;
    }

    private int buscarIndiceClasse(Instances dados, String classe) {
        String alvo = normalizar(classe);
        for (int i = 0; i < dados.numAttributes(); i++) {
            if (normalizar(dados.attribute(i).name()).equals(alvo)) {
                return i;
            }
        }
        return -1;
    }

    private String normalizar(String texto) {
        return texto.replace("\uFEFF", "").trim().toLowerCase(Locale.ROOT);
    }
}
