package com.google.ar.sceneform.samples.ARFurniture;

import static java.util.concurrent.TimeUnit.SECONDS;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.ArraySet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GltfActivity extends AppCompatActivity {
  private static final String TAG = GltfActivity.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;

  private ArFragment arFragment;
  private Renderable renderable;
  private FloatingActionButton rotate;
  private boolean isRotate = false;
  private static class AnimationInstance {
    Animator animator;
    Long startTime;
    float duration;
    int index;

    AnimationInstance(Animator animator, int index, Long startTime) {
      this.animator = animator;
      this.startTime = startTime;
      this.duration = animator.getAnimationDuration(index);
      this.index = index;
    }
  }

  private final Set<AnimationInstance> animators = new ArraySet<>();

  private final List<Color> colors =
      Arrays.asList(
          new Color(0, 0, 0, 1),
          new Color(1, 0, 0, 1),
          new Color(0, 1, 0, 1),
          new Color(0, 0, 1, 1),
          new Color(1, 1, 0, 1),
          new Color(0, 1, 1, 1),
          new Color(1, 0, 1, 1),
          new Color(1, 1, 1, 1));
  private int nextColor = 0;

  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // CompletableFuture requires api level 24
  // FutureReturnValueIgnored is not valid
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!checkIsSupportedDeviceOrFinish(this)) {
      return;
    }

    setContentView(R.layout.activity_ux);
    initModel("https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb");
  }

  public void initModel(String path) {
      arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

      WeakReference<GltfActivity> weakActivity = new WeakReference<>(this);

      ModelRenderable.builder()
              .setSource(
                      this,
                      Uri.parse(path))
              .setIsFilamentGltf(true)
              .build()
              .thenAccept(
                      modelRenderable -> {
                          GltfActivity activity = weakActivity.get();
                          if (activity != null) {
                              activity.renderable = modelRenderable;
                          }
                      })
              .exceptionally(
                      throwable -> {
                          Toast toast =
                                  Toast.makeText(this, "Unable to load Tiger renderable", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                          return null;
                      });

      arFragment.setOnTapArPlaneListener(
              (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                  if (renderable == null) {
                      return;
                  }

                  // Create the Anchor.
                  Anchor anchor = hitResult.createAnchor();
                  AnchorNode anchorNode = new AnchorNode(anchor);
                  anchorNode.setParent(arFragment.getArSceneView().getScene());

                  // Create the transformable model and add it to the anchor.
//                  TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
//                  model.setParent(anchorNode);
//                  model.setRenderable(renderable);
//
//
//                  model.select();

                  RotatingNode showNode = new RotatingNode(arFragment.getTransformationSystem());
                  showNode.setRenderable(renderable);
                  showNode.setParent(anchorNode);

                  showNode.setOnTapListener(new Node.OnTapListener() {
                      @Override
                      public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                          FloatingActionButton button2 = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
                          button2.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  arFragment.getArSceneView().getScene().removeChild(anchorNode);
                              }
                          });
                          rotate = (FloatingActionButton) findViewById(R.id.floatingActionButton);
                          rotate.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  if(isRotate == false){
                                      showNode.startAnimation();
                                      isRotate = true;
                                  }
                                  else{
                                      showNode.stopAnimation();
                                      isRotate = false;
                                  }
                              }
                          });
                      }
                  });

                  FilamentAsset filamentAsset = showNode.getRenderableInstance().getFilamentAsset();
                  if (filamentAsset.getAnimator().getAnimationCount() > 0) {
                      animators.add(new AnimationInstance(filamentAsset.getAnimator(), 0, System.nanoTime()));
                  }

                  Color color = colors.get(nextColor);
                  nextColor++;
                  for (int i = 0; i < renderable.getSubmeshCount(); ++i) {
                      Material material = renderable.getMaterial(i);
                      material.setFloat4("baseColorFactor", color);
                  }

                  arFragment.getArSceneView()
                          .getScene()
                          .addOnUpdateListener(
                                  frameTime -> {
                                      Long time = System.nanoTime();
                                      for (AnimationInstance animator : animators) {
                                          animator.animator.applyAnimation(
                                                  animator.index,
                                                  (float) ((time - animator.startTime) / (double) SECONDS.toNanos(1))
                                                          % animator.duration);
                                          animator.animator.updateBoneMatrices();
                                      }
                                  });
              });

  }

  /**
   * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
   * on this device.
   *
   * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
   *
   * <p>Finishes the activity if Sceneform can not run
   */
  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
      activity.finish();
      return false;
    }
    String openGlVersionString =
        ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
          .show();
      activity.finish();
      return false;
    }
    return true;
  }
}
