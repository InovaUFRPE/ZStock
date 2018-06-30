package com.zstok.historico.negocio;

import com.google.firebase.database.DataSnapshot;
import com.zstok.historico.persistencia.HistoricoDAO;

public class HistoricoServices {
    public static boolean gerarHistorico(DataSnapshot dataSnapshot){
        return HistoricoDAO.gerarHistorico(dataSnapshot);
    }
}
