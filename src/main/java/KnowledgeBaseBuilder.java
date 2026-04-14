import java.io.File;

public class KnowledgeBaseBuilder {

    public static void main(String[] args) throws Exception {
        File pastaProjeto = new File(System.getProperty("user.dir"));
        File pastaResultados = new File(pastaProjeto, "results");
        if (!pastaResultados.exists()) {
            pastaResultados.mkdirs();
        }

        ProcessadorBase processador = new ProcessadorBase(pastaProjeto, pastaResultados);
        processador.processar("hr", "WA_Fn-UseC_-HR-Employee-Attrition.csv", "Attrition");
        processador.processar("education", "academic success.csv", "Target");

        System.out.println("Concluído.");
    }
}
