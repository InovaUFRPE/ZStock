package com.zstok.historico.negocio;

import com.zstok.historico.dominio.Historico;
import com.zstok.historico.persistencia.HistoricoDAO;

public class HistoricoServices {
    public static boolean adicionarHistorico(Historico historico){
        return HistoricoDAO.adicionarHistorico(historico);
    }
}
