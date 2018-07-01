package com.zstok.historico.persistencia;

import com.google.firebase.database.DataSnapshot;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.utils.FirebaseController;

public class HistoricoDAO {
    public static boolean adicionarHistorico(Historico historico){
        FirebaseController.getFirebase().child("historico").push().setValue(historico);
        return true;
    }
}
