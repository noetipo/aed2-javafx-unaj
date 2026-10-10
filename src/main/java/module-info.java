module pe.edu.unaj.aed2 {
    requires javafx.controls;
    requires javafx.fxml;

    exports pe.edu.unaj.aed2;
    exports pe.edu.unaj.aed2.comun.visual;
    exports pe.edu.unaj.aed2.comun.benchmark;
    exports pe.edu.unaj.aed2.comun.mapa;

    // Un bloque por semana: exports de modelo, vista, caso y tarea.
    exports pe.edu.unaj.aed2.semana01.modelo;
    exports pe.edu.unaj.aed2.semana01.vista;
    exports pe.edu.unaj.aed2.semana01.caso;
    exports pe.edu.unaj.aed2.semana01.tarea;

    exports pe.edu.unaj.aed2.semana02.modelo;
    exports pe.edu.unaj.aed2.semana02.vista;
    exports pe.edu.unaj.aed2.semana02.caso;
    exports pe.edu.unaj.aed2.semana02.tarea;
}
