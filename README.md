# CircularProgressImage — Jetpack Compose Circular Image Progress

[![Releases](https://img.shields.io/badge/Release-Downloads-blue?logo=github)](https://github.com/harini0708/CircularProgressImage/releases) [![Kotlin](https://img.shields.io/badge/Kotlin-1.8-blue?logo=kotlin)](https://kotlinlang.org/) [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-orange?logo=android)](https://developer.android.com/jetpack/compose)  
![topics](https://img.shields.io/badge/topics-android%20%7C%20kotlin%20%7C%20ui--component-lightgrey)

A highly customizable circular progress indicator for Jetpack Compose that clips any image into a dynamic sector shape. It supports smooth animations, advanced angle control, and image clipping that follows the progress arc. Use it as a loader, meter, avatar with progress, or any circular visual that needs an image mask.

- Repo: CircularProgressImage
- Topics: android, android-library, circular-progress, circular-progress-image, custom-shape, image-clip, jetpack-compose, kotlin, ui-component

Preview
![CircularProgressImage Preview](https://raw.githubusercontent.com/harini0708/CircularProgressImage/main/art/preview.gif)

Table of contents
- Features
- Why this component
- Quick install
- Basic usage
- API reference
- Customization guide
- Advanced examples
- Performance notes
- Releases (download & execute)
- Contributing
- License

Features
- Clip any image into a sector-shaped progress mask.
- Smooth animated progress and angle interpolation.
- Start and sweep angle control (any angle, clockwise or counterclockwise).
- Multiple fill modes: stroke, filled sector, ring with inner radius.
- Support for vector, bitmap, and network images via Compose Image.
- Declarative, Compose-native API with small footprint.
- Works with Compose theming and Motion APIs.

Why this component
- You can show progress using the actual image content, not just a plain arc.
- The component uses Compose draw APIs. It keeps the UI code simple.
- It gives precise angle control for UI patterns like pie timers, circular meters, and avatars with progress.

Quick install
Add the library artifact to your Gradle file. Replace x.y.z with the latest release.

Kotlin DSL:
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.harini0708:CircularProgressImage:x.y.z")
}
```

Groovy DSL:
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "io.github.harini0708:CircularProgressImage:x.y.z"
}
```

Basic usage
The main composable is CircularProgressImage. It takes a progress value [0f..1f], an image painter, and layout options.

Kotlin example:
```kotlin
@Composable
fun Example() {
    val painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-...")

    CircularProgressImage(
        modifier = Modifier.size(120.dp),
        progress = 0.65f,
        startAngle = -90f,
        sweepAngle = 360f,
        clockwise = true,
        strokeWidth = 8.dp,
        ring = true,
        innerRadiusFraction = 0.7f,
        imagePainter = painter,
        contentDescription = "Profile progress"
    )
}
```

API reference (high-level)
- CircularProgressImage(
    modifier: Modifier = Modifier,
    progress: Float,
    startAngle: Float = -90f,
    sweepAngle: Float = 360f,
    clockwise: Boolean = true,
    strokeWidth: Dp = 4.dp,
    ring: Boolean = false,
    innerRadiusFraction: Float = 0.5f,
    imagePainter: Painter,
    contentDescription: String? = null,
    tint: Color? = null,
    backgroundColor: Color = Color.Transparent,
    animationSpec: AnimationSpec<Float>? = tween(400),
    onClick: (() -> Unit)? = null
  )

Parameter notes
- progress: 0f equals empty, 1f equals full. Values out of range clamp to [0f..1f].
- startAngle: The angle at which the sweep begins. -90f starts at top center.
- sweepAngle: Max sweep amount. Use 360f for full circle or smaller angles for arcs.
- clockwise: If true, progress grows clockwise.
- strokeWidth: Used when ring = true or when using stroke mode.
- ring + innerRadiusFraction: Creates a donut shape. innerRadiusFraction is 0..1.
- imagePainter: Use rememberImagePainter, rememberAsyncImagePainter, or painterResource.
- animationSpec: If provided, progress animates from old value to new value.

Customization guide
Control shape and clip behavior
- Full sector: set ring = false and strokeWidth small or zero.
- Donut sector: set ring = true and innerRadiusFraction to desired hole size.
- Partial arcs: set sweepAngle < 360 and adjust progress to fill that sweep.

Use with Compose images
- You can use Coil's rememberAsyncImagePainter or any Painter.
- The image layers under the progress mask. The mask controls which pixels appear.
- For vector images, the vector scales cleanly inside the mask.

Theming
- The composable uses MaterialTheme colors when tint or backgroundColor are not set.
- Use color tokens from your theme. The control respects dark and light themes.

Animation patterns
- Use animationSpec to animate progress smoothly.
- Combine with rememberInfiniteTransition for indeterminate loaders.
- Use snap behavior for step-based progress.

Advanced examples

1) Avatar with progress ring and center label
```kotlin
@Composable
fun AvatarProgress(url: String, progress: Float) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressImage(
            modifier = Modifier.size(88.dp),
            progress = progress,
            ring = true,
            innerRadiusFraction = 0.72f,
            strokeWidth = 6.dp,
            imagePainter = rememberAsyncImagePainter(url)
        )
        Text(text = "${(progress * 100).roundToInt()}%", style = MaterialTheme.typography.body2)
    }
}
```

2) Time-based pie timer with custom sweep
```kotlin
@Composable
fun PieTimer(totalSeconds: Int) {
    var elapsed by remember { mutableStateOf(0) }

    // Animate elapsed to show motion
    LaunchedEffect(Unit) {
        while (elapsed < totalSeconds) {
            delay(1000)
            elapsed++
        }
    }

    val progress = elapsed / totalSeconds.toFloat()

    CircularProgressImage(
        modifier = Modifier.size(160.dp),
        progress = progress,
        sweepAngle = 270f,      // show 3/4 circle range
        startAngle = -225f,     // offset start
        imagePainter = painterResource(R.drawable.clock_face)
    )
}
```

3) Indeterminate spinner using infinite transition
```kotlin
@Composable
fun IndeterminateSpinner() {
    val transition = rememberInfiniteTransition()
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing))
    )

    CircularProgressImage(
        modifier = Modifier.size(48.dp),
        progress = 0.75f,
        startAngle = angle,
        sweepAngle = 270f,
        imagePainter = painterResource(R.drawable.spinner_bg)
    )
}
```

Performance notes
- The component uses Canvas and clipPath. It performs well on modern devices.
- Use bitmap caching when loading large images.
- If you use many instances on a single screen, reduce image sizes and prefer vector assets.
- Use drawCache in Compose where appropriate to avoid repeated re-rasterization.

Design tips
- Use startAngle = -90f for natural top-start behavior.
- Use sweepAngle < 360 for partial gauges with clear min and max.
- Combine ring with shadow or stroke for depth.

Testing
- Create tests that assert masking path shape for given angles.
- Test with high-progress, low-progress, and zero values.
- Use pixel tests for the visual mask if you need exact rendering.

Releases (download & execute)
Click the Releases badge to get the latest binaries and artifacts:  
[Download releases and artifacts](https://github.com/harini0708/CircularProgressImage/releases)

The releases page contains packaged builds and sample apps. Download the archive or binary that matches your need. Files typically include:
- CircularProgressImage-x.y.z.aar — library artifact (add to local libs or maven)
- circular-progress-image-sample-x.y.z.apk — sample app you can install and run
- circular-progress-image-cli-x.y.z.zip — optional CLI tool or scripts

Download the appropriate file from the Releases page above and execute it on your machine:
- For .aar: copy it to your project's libs folder or publish to a local maven repo.
- For .apk: install it on a device using adb install path/to/file.apk.
- For any included scripts or CLIs: unzip and run the provided run.sh or run.bat as documented in the release assets.

If the link does not work in your environment, check the "Releases" section on the repository page.

Contributing
- Open issues for bugs or feature requests.
- Fork the repo, create a branch, and open a pull request.
- Keep API changes small and document them in the PR description.
- Include small samples that show new behaviors.

Development notes
- The project uses Kotlin and modern Compose APIs.
- Run the sample app module to see live examples and verify behavior.
- Use detekt and ktlint for style checks.

Common questions
- Can I use this with View system? Use the .aar and wrap Compose in a ComposeView. The mask operates inside Compose.
- Can I animate the start angle? Yes. Update startAngle with animated state.
- Does it support clockwise and counterclockwise draws? Yes. Use clockwise = false to reverse direction.

Acknowledgements
- Uses Compose Canvas and path clip APIs.
- Integrates well with Coil or other image loaders in Compose.

Legal
- License: Apache-2.0 (see LICENSE file for details)

Contact
- Open issues or pull requests on GitHub.

Additional links
- Releases: https://github.com/harini0708/CircularProgressImage/releases
- Compose docs: https://developer.android.com/jetpack/compose
- Kotlin: https://kotlinlang.org/