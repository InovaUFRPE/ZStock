package com.zstok.historico.persistencia;

import com.google.firebase.database.DataSnapshot;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.utils.FirebaseController;

public class HistoricoDAO {
    public static boolean adicionarHistorico(Historico historico){
        historico.setIdHistorico(FirebaseController.getFirebase().push().getKey());
        FirebaseController.getFirebase().child("historico").child(historico.getIdHistorico()).setValue(historico);
        return true;
    }
}
