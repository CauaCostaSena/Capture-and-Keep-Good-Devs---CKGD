package com.ckgd.config;

import com.ckgd.entity.PlanoDeAssinatura;
import com.ckgd.repository.PlanoDeAssinaturaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Garante que exista ao menos um plano de assinatura (Free) cadastrado,
 * necessário para o fluxo de cadastro de empresas funcionar "out of the box"
 * mesmo que o script database/03_data_manipulation.sql não tenha sido executado.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final PlanoDeAssinaturaRepository planoRepository;

    public DataSeeder(PlanoDeAssinaturaRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    @Override
    public void run(String... args) {
        if (planoRepository.count() == 0) {
            PlanoDeAssinatura free = new PlanoDeAssinatura();
            free.setNomePlano("Free");
            free.setPrecoPlano(BigDecimal.ZERO);
            free.setPeriodicidade(PlanoDeAssinatura.Periodicidade.MENSAL);
            free.setLimiteRequisicao(10);
            free.setLimiteAvaliacao(5);
            free.setLimiteComparacao(3);
            free.setStatusPlano(PlanoDeAssinatura.StatusPlano.ATIVO);
            free.setDataAtivacao(LocalDate.now());
            planoRepository.save(free);

            PlanoDeAssinatura pro = new PlanoDeAssinatura();
            pro.setNomePlano("Pro");
            pro.setPrecoPlano(new BigDecimal("199.90"));
            pro.setPeriodicidade(PlanoDeAssinatura.Periodicidade.MENSAL);
            pro.setLimiteRequisicao(200);
            pro.setLimiteAvaliacao(100);
            pro.setLimiteComparacao(50);
            pro.setStatusPlano(PlanoDeAssinatura.StatusPlano.ATIVO);
            pro.setDataAtivacao(LocalDate.now());
            planoRepository.save(pro);

            PlanoDeAssinatura enterprise = new PlanoDeAssinatura();
            enterprise.setNomePlano("Enterprise");
            enterprise.setPrecoPlano(BigDecimal.ZERO);
            enterprise.setPeriodicidade(PlanoDeAssinatura.Periodicidade.ANUAL);
            enterprise.setLimiteRequisicao(0); // ilimitado
            enterprise.setLimiteAvaliacao(0);
            enterprise.setLimiteComparacao(0);
            enterprise.setStatusPlano(PlanoDeAssinatura.StatusPlano.ATIVO);
            enterprise.setDataAtivacao(LocalDate.now());
            planoRepository.save(enterprise);
        }
    }
}
