package com.example.livedata_entrenador;

import static java.util.concurrent.TimeUnit.SECONDS;

import androidx.lifecycle.LiveData;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class Entrenador {
    interface EntrenadorListener{
        void cuandoDeLaOrden(String orden);
    }

    Random random = new Random();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    ScheduledFuture<?> entrenando;

    LiveData<String> ordenLiveData = new LiveData<String>() {
        @Override
        protected void onActive() {
            super.onActive();

            iniciarEntrenamiento(new EntrenadorListener() {
                @Override
                public void cuandoDeLaOrden(String orden) {
                    postValue(orden);
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();

            pararEntrenamiento();
        }
    };

    void iniciarEntrenamiento(EntrenadorListener entrenadorListener) {
        if (entrenando == null || entrenando.isCancelled()) {
            // Programar la tarea para ejecutarse a una tasa fija
            entrenando = scheduler.scheduleAtFixedRate(new Runnable() {
                int ejercicio;
                int repeticiones = -1;

                // Método run, que se ejecutará repetidamente según la tasa fija
                @Override
                public void run() {
                    if (repeticiones < 0) {
                        // Genera un número aleatorio entre 3 (inclusive) y 6 (exclusive)
                        repeticiones = random.nextInt(3) + 3;
                        // Genera un número aleatorio entre 1 (inclusive) y 6 (exclusive)
                        ejercicio = random.nextInt(5)+1;
                    }
                    // Llama al método del listener para informar sobre la orden
                    entrenadorListener.cuandoDeLaOrden("EJERCICIO" + ejercicio + ":" + (repeticiones == 0 ? "CAMBIO" : repeticiones));
                    repeticiones--;
                }
            }, 0, 1, SECONDS);// Demora inicial de 0 segundos, tasa fija de 1 segundo entre ejecuciones
        }
    }

    // Método para detener el entrenamiento
    void pararEntrenamiento() {
        // Cancela la tarea de entrenamiento si está en progreso
        if (entrenando != null) {
            entrenando.cancel(true);
        }
    }
}
