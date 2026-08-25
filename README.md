# Weather App (Android)

MVVM Android build of the same weather app as the iOS version in the
sibling `WeatherApp/` folder — search a US city or use current location, see
conditions and an icon, remembered across launches.

This covers the Android-specific "must have" and "nice to have" list from
the challenge; see the root [README.md](../README.md) for what the challenge
is asking for in general terms.

## 1. What's different from a typical single-language app

The brief specifically asks for **a combination of Java and Kotlin**
("to demonstrate the use of Java"), on top of MVVM + Retrofit + JUnit as
must-haves, with Coroutines, Compose, Hilt, and Jetpack Navigation as
nice-to-haves. Concretely, in this codebase:

- **Java**: `data/remote/model/CurrentWeatherResponse.java` and
  `GeocodingResult.java` (the API response DTOs, using Gson annotations),
  and `util/WeatherFormatUtils.java` (static string-formatting helpers).
  Both have a matching JUnit test written in Java too
  (`WeatherFormatUtilsTest.java`).
- **Kotlin**: everything else — ViewModel, repositories, DI modules, Compose
  UI, and the rest of the tests. Kotlin code calls the Java classes directly
  (`response.main.temp` reads a Java getter as a Kotlin property;
  `WeatherFormatUtils.formatTemperature(...)` calls a Java static method) —
  that interop is the actual point being demonstrated, not just "some files
  are .java".

## 2. Before you touch Android Studio: get an API key

Same key as the iOS build. Create a free account at
[openweathermap.org](https://openweathermap.org/), grab your API key under
**API keys**. It can take up to a couple of hours to activate.

## 3. What you need to run this

Android Studio (Hedgehog/2023.1+ or newer) with an SDK for API 34 installed.
Unlike the iOS half of this challenge, this **can** be built on Windows,
Linux, or macOS — Android's toolchain isn't platform-locked the way Xcode
is.

The Gradle wrapper checked into this repo (`gradlew`/`gradlew.bat` +
`gradle/wrapper/`) pins **Gradle 8.7**, which is the documented minimum for
AGP 8.5.2 and is what Kotlin 1.9.24's `kapt` plugin (used for Hilt's
annotation processing) was built against — Gradle 9.x removed a Gradle API
that old `kapt` still calls, so don't let Android Studio "upgrade" the
wrapper if it offers to.

**JDK note:** Gradle 8.7 can only run on JDK 21 or older. If Android Studio
is bundling a newer JDK (its embedded JBR — check **Settings → Build,
Execution, Deployment → Build Tools → Gradle → Gradle JDK**), sync will fail
with a cryptic `Configuration.fileCollection(Spec)` or a bare version-number
error. Point that "Gradle JDK" dropdown at a JDK 17 or 21 instead — Android
Studio's "Download JDK..." option in that same dropdown can fetch one for
you if none is listed.

## 4. Running it

1. Open Android Studio → **Open** → select this `WeatherApp-Android/`
   folder. Let it sync (first sync will download the pinned Gradle
   distribution, AGP, and dependencies — see the JDK note above if sync
   fails).
2. Copy `local.properties.example` to `local.properties` in this same
   folder — or, if Android Studio already generated a `local.properties`
   with `sdk.dir` in it during sync, just add the `OWM_API_KEY=...` line to
   that file. Put your real API key in.
3. Run (▶) on an emulator or device running API 26+. First launch prompts
   for location permission — try both **Allow** and **Don't allow** to see
   both code paths (see section 6).
4. Run unit tests: right-click `app/src/test` → **Run 'Tests in...'**, or
   from the command line: `./gradlew testDebugUnitTest` (Windows:
   `.\gradlew.bat testDebugUnitTest`).

## 5. Permissions — what's actually implemented

The brief explicitly calls out "make sure you are correctly handling any
necessary permissions," so to be specific about what that means here:

- **Manifest declarations**: `ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`,
  `INTERNET` in `AndroidManifest.xml`. Declaring them is necessary but not
  sufficient — Android still requires a **runtime** request for location
  (a "dangerous" permission) on API 23+.
- **Runtime request**: `WeatherRoute.kt` checks
  `ContextCompat.checkSelfPermission` first; only if not already granted
  does it launch `ActivityResultContracts.RequestPermission()`, which shows
  the system dialog. This happens once automatically on first screen
  appearance (to satisfy "ask the user for location access"), and again on
  demand if the user taps "Use Current Location" without having granted it
  yet.
- **Only Activity/Compose code touches the permission APIs.** `WeatherViewModel`
  never imports anything permission-related — it just receives a
  `hasLocationPermission: Boolean` from the caller and decides what to do
  with it. This is a deliberate difference from the iOS build, where
  `CLLocationManager` can both check *and* request permission itself: on
  Android, only an `Activity` can show the system dialog, so requesting has
  to live in the UI layer. Keeping that boundary explicit is what keeps the
  ViewModel's decision logic unit-testable without Robolectric or an
  instrumented test.
- **Denial is handled gracefully, not treated as a crash/dead-end**: if
  permission is denied, the ViewModel falls back to the last searched city
  (if any), otherwise shows the idle "search for a city" prompt — never a
  raw error screen. See `WeatherViewModel.onAppear` / `onUseCurrentLocationTapped`.
- **Not implemented**: a "permanently denied — open Settings" deep link
  (`shouldShowRequestPermissionRationale` / rationale UI). Worth adding next;
  omitted here to keep the permission flow's surface area small enough to
  read in one sitting.

## 6. Architecture, and why it's laid out this way

```
app/src/main/java/com/weatherapp/android/
  WeatherApplication.kt      @HiltAndroidApp entry point
  MainActivity.kt             Single Activity; hosts Compose + Navigation
  navigation/                 WeatherNavHost — Jetpack Navigation Compose
  di/                         Hilt modules (NetworkModule, RepositoryModule)
  domain/                     WeatherError, Coordinates, ResolvedLocation
  data/
    remote/                   Retrofit interface + Java DTOs
    repository/                WeatherRepository, PersistenceRepository (+ impls)
    location/                  LocationProvider (+ impl, plain android.location)
    cache/                      WeatherIconCache (+ impl, memory + disk)
  ui/
    weather/                   WeatherViewModel, WeatherUiState/Model/Mapper,
                                WeatherRoute (stateful) + WeatherScreen (stateless),
                                WeatherCard, StatusMessage
    theme/                      Compose Material3 theme
  util/                        WeatherFormatUtils.java

app/src/test/java/com/weatherapp/android/
  Fixtures.kt, MainDispatcherRule.kt
  fakes/                       Hand-written test doubles for every interface
  ui/weather/                  WeatherViewModelTest, WeatherUiMapperTest
  data/repository/             WeatherRepositoryImplTest (Mockito)
  util/                        WeatherFormatUtilsTest.java
```

**Data flow for a search:** `WeatherScreen` (Compose) → `WeatherViewModel.onSearchSubmitted()`
→ `WeatherRepository.geocodeCity()` (city name → lat/lon, since
OpenWeatherMap deprecated city-name lookups on the weather endpoint itself)
→ `WeatherRepository.getCurrentWeather()` (lat/lon → conditions) →
`WeatherUiMapper` turns the raw (Java) response into a `WeatherUiModel`
(formatted strings) → `WeatherViewModel`'s `StateFlow<WeatherUiState>`
triggers recomposition, and separately fetches/caches the icon through
`WeatherIconCache`.

**Why MVVM + a Route/Screen split:** `WeatherViewModel` exposes only
`StateFlow`s and plain functions — no Compose, no Android `Context`, no
platform types beyond `android.graphics.Bitmap`. `WeatherRoute` is the
"stateful" composable that wires the ViewModel (via `hiltViewModel()`) and
Android permission APIs together; `WeatherScreen` is a "stateless" composable
that's a pure function of its parameters. That split is what makes
`WeatherScreen` trivially previewable and keeps all the actual decision
logic sitting in one testable place (the ViewModel) rather than smeared
across composables.

**Why DI (Hilt) matters here specifically:** every service —
`WeatherRepository`, `LocationProvider`, `PersistenceRepository`,
`WeatherIconCache` — is an interface bound in `RepositoryModule`, with the
real implementation only ever constructed by Hilt. `WeatherViewModelTest`
swaps in the `fakes/` package's hand-written doubles instead — no mocking
framework needed there, no real network/location/SharedPreferences touched.

**Auto-load / location precedence:** matches the iOS build's resolution of
the same ambiguity in the brief (see the iOS README's section 5): granted
location wins as the default; otherwise fall back to the last searched
city; otherwise show the empty prompt. Implemented in
`WeatherViewModel.onAppear`.

**Icon caching:** `WeatherIconCacheImpl` is a two-tier cache — in-memory
`LruCache` plus a small folder under the app's cache directory — reusing
the same `OkHttpClient` Retrofit already depends on, rather than adding
Coil/Glide for what's one GET request and a bitmap decode.

**Error handling:** every failure path (network, HTTP status, empty
geocode match, location denied/unavailable, blank input) is normalized into
`WeatherError`, a sealed class carrying a `userMessage` meant to be read by
an actual person — `WeatherScreen` never sees a raw `IOException` or HTTP
code.

## 7. Deliberate simplifications (flagged per the brief's request)

- Location uses plain `android.location.LocationManager`, not Play
  Services' `FusedLocationProviderClient`. That keeps the app free of a
  Google Play Services dependency for what's a single "get me a rough fix"
  request; the trade-off is a slightly more manual one-shot listener
  (`LocationProviderImpl`) instead of `getCurrentLocation()`'s one-liner.
- No "permanently denied → open Settings" flow for location permission
  (see section 5).
- `PersistenceRepositoryImpl` (the real `SharedPreferences` wrapper) doesn't
  have its own unit test — it needs a real Android `Context`, which means
  either Robolectric or an instrumented (`androidTest`) test, and the brief
  only requires plain JUnit. `WeatherViewModelTest` covers its *usage*
  through `FakePersistenceRepository` instead. Given more time, an
  instrumented test for the real implementation would be the next addition.
- No UI/instrumented test target (Espresso is called out as nice-to-have,
  not must-have) — `WeatherScreen`'s stateless design (section 6) is meant
  to make adding Compose UI tests straightforward later, but none are
  included here.
- The layout is a single vertical column; it reflows correctly for
  orientation and different screen sizes but doesn't add a dedicated wide/
  two-pane layout for tablets — functionally adaptive, not a distinct
  large-screen design.

## 8. Running the tests

`app/src/test` covers:
- `WeatherViewModelTest` — every state-transition scenario from the
  precedence rule in section 6, plus search success/failure and the two
  location-permission paths, all against hand-written fakes (no network, no
  real permission prompts, no Android framework calls).
- `WeatherRepositoryImplTest` — success/error-code mapping against a
  **Mockito**-mocked `OpenWeatherApiService` (401 → InvalidApiKey, empty
  geocode result → CityNotFound, `IOException` → NoConnectivity).
- `WeatherUiMapperTest` — formatting correctness off a fixed JSON fixture
  (`Fixtures.kt`), exercising the Java `WeatherFormatUtils` through the
  Kotlin mapper.
- `WeatherFormatUtilsTest.java` — the Java formatting utility tested
  directly, in Java.

Run with `./gradlew testDebugUnitTest` (after Android Studio has generated
the Gradle wrapper on first open — see section 4) or via the IDE's test
runner.
