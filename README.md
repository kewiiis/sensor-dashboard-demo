## Architecture (MVVM + Flow)

Data path:
`sensors/AndroidSensorDataSource` → `data/SensorRepository(Impl)` → `SensorsViewModel (StateFlow)` → UI (Compose)

### Public API for UI (stable)

- ViewModel:
    - `val state: StateFlow<UiState>`
    - `fun selectSensor(type: SensorType)`
    - `fun setRate(rate: SamplingRate)`

- UiState:
    - `available: List<SensorType>`
    - `currentType: SensorType?`
    - `rate: SamplingRate`
    - `lastReading: SensorReading?`
    - `error: String?`

- Models:
    - `enum class SensorType { ACCELEROMETER, GYROSCOPE, LIGHT }`
    - `data class SensorReading(val type: SensorType, val values: FloatArray, val timestampNanos: Long)`
    - `enum class SamplingRate { UI, NORMAL, GAME, FASTEST }`

### How to use (UI side)

- **Dashboard screen**
    - Observe `vm.state`
    - Read `state.available` to list sensors
    - On click → `vm.selectSensor(selectedType)` → navigate to Detail

- **Detail screen**
    - Observe `vm.state.lastReading` to display live values
    - Let user choose a **SamplingRate** and call `vm.setRate(rate)`
    - Display basic error UI if `state.error != null`
  
(It's juste an exemple for the projection but u can do as u want)

### File locations

- `app/src/main/java/com/isep/sensordashboard/sensors/`
    - `AndroidSensorDataSource.kt`, `SamplingRate.kt`
- `app/src/main/java/com/isep/sensordashboard/data/`
    - `SensorRepository.kt`, `SensorRepositoryImpl.kt`
- `app/src/main/java/com/isep/sensordashboard/model/`
    - `SensorType.kt`, `SensorReading.kt`
- `app/src/main/java/com/isep/sensordashboard/`
    - `SensorsViewModel.kt`
