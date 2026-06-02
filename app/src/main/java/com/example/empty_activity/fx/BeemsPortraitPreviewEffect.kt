import androidx.camera.core.CameraEffect
import androidx.camera.core.SurfaceRequest
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PortraitPreviewEffect : CameraEffect(CameraEffect.PREVIEW, getExecutor(), getSurfaceProcessor()) {

    companion object {
        private fun getExecutor(): Executor {
            // Returns an executor for calling the SurfaceProcessor
            return Executors.newSingleThreadExecutor()
        }

        private fun getSurfaceProcessor(): SurfaceProcessor {
            // Return a SurfaceProcessor implementation that applies a portrait effect.
            return PortraitSurfaceProcessor()
        }
    }
}