// Pomodoro Timer Glyph Toy for glyph.andreibanu.com simulator
function createPomodoroTimer() {
    const manager = new GlyphMatrixManager();

    // Timer constants
    const WORK_DURATION = 25 * 60; // 25 minutes in seconds
    const BREAK_DURATION = 5 * 60; // 5 minutes in seconds

    // Timer state
    let currentTimeSeconds = 0;
    let isWorkSession = true; // true for work, false for break
    let isRunning = false;
    let timerStarted = false;
    let timerInterval = null;

    // Register event handler for button interactions
    registerGlyphEventHandler((eventType) => {
        switch (eventType) {
            case GlyphToy.EVENT_CHANGE:
                console.log('Long press detected!');
                if (!timerStarted || !isRunning) {
                    // Start the timer
                    startTimer();
                } else {
                    // Skip to next phase
                    skipToNext();
                }
                break;

            case GlyphToy.EVENT_ACTION_DOWN:
                console.log('Button pressed down');
                break;

            case GlyphToy.EVENT_ACTION_UP:
                console.log('Button released');
                break;
        }
    });

    function startTimer() {
        console.log('Starting Pomodoro timer');
        if (!timerStarted) {
            // First time starting - begin with work session
            isWorkSession = true;
            currentTimeSeconds = WORK_DURATION;
            timerStarted = true;
        }

        isRunning = true;
        updateDisplay();

        // Start the countdown
        timerInterval = setInterval(() => {
            if (isRunning && currentTimeSeconds > 0) {
                currentTimeSeconds--;
                updateDisplay();
            } else if (isRunning && currentTimeSeconds <= 0) {
                // Timer finished, switch to next phase
                switchToNextPhase();
            }
        }, 1000);
    }

    function switchToNextPhase() {
        console.log(`Switching from ${isWorkSession ? 'work' : 'break'} to ${isWorkSession ? 'break' : 'work'}`);
        isWorkSession = !isWorkSession;
        currentTimeSeconds = isWorkSession ? WORK_DURATION : BREAK_DURATION;
        isRunning = true;
        updateDisplay();
    }

    function skipToNext() {
        console.log('Skipping to next phase');
        if (timerInterval) {
            clearInterval(timerInterval);
        }
        switchToNextPhase();

        // Restart the interval
        timerInterval = setInterval(() => {
            if (isRunning && currentTimeSeconds > 0) {
                currentTimeSeconds--;
                updateDisplay();
            } else if (isRunning && currentTimeSeconds <= 0) {
                switchToNextPhase();
            }
        }, 1000);
    }

    function displayWaitingState() {
        console.log('Displaying waiting state');
        const waitingBitmap = createTextBitmap('LONG PRESS\nTO START', 255);

        const object = new GlyphMatrixObject.Builder()
            .setImageSource(waitingBitmap)
            .setPosition(0, 0)
            .setBrightness(200)
            .setScale(100)
            .build();

        const frame = new GlyphMatrixFrame.Builder()
            .addTop(object)
            .build();

        manager.setMatrixFrame(frame);
    }

    function updateDisplay() {
        const minutes = Math.floor(currentTimeSeconds / 60);
        const seconds = currentTimeSeconds % 60;
        const timeText = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        const phaseText = isWorkSession ? 'WORK' : 'BREAK';

        console.log(`${phaseText}: ${timeText}`);

        // Create timer display with clear numbers
        const timerBitmap = createTimerBitmap(timeText, phaseText, isWorkSession);

        const object = new GlyphMatrixObject.Builder()
            .setImageSource(timerBitmap)
            .setPosition(0, 0)
            .setBrightness(255) // Full brightness for clear visibility
            .setScale(100)
            .build();

        const frame = new GlyphMatrixFrame.Builder()
            .addTop(object)
            .build();

        manager.setMatrixFrame(frame);
    }

    function createTextBitmap(text, color) {
        // Create a simple bitmap representation for the simulator
        // In the actual simulator, this would be handled by the simulator's bitmap creation
        return {
            type: 'text',
            content: text,
            color: color,
            size: 'small',
            align: 'center'
        };
    }

    function createTimerBitmap(timeText, phaseText, isWorkSession) {
        // Create bitmap optimized for circular 25x25 display
        const content = `${phaseText}\n${timeText}\n${isWorkSession ? '●' : '○'}`;

        return {
            type: 'timer',
            content: content,
            timeText: timeText,
            phaseText: phaseText,
            isWorkSession: isWorkSession,
            color: 255, // White
            layout: 'circular'
        };
    }

    // Initialize the manager
    manager.init(() => {
        console.log('Pomodoro Timer initialized');
        manager.register(Glyph.DEVICE_23112);
        displayWaitingState();
        console.log('Pomodoro Timer ready! Long press to start your 25-minute work session.');
        console.log('Long press again anytime to skip to the next phase.');
    });

    // Cleanup function
    function cleanup() {
        if (timerInterval) {
            clearInterval(timerInterval);
        }
        console.log('Pomodoro Timer stopped');
    }

    return {
        manager: manager,
        cleanup: cleanup,
        getState: () => ({
            timeRemaining: currentTimeSeconds,
            isWorkSession: isWorkSession,
            isRunning: isRunning,
            timerStarted: timerStarted
        })
    };
}

// Start the Pomodoro Timer
console.log('🍅 Starting Pomodoro Timer Glyph Toy');
console.log('==================================');
console.log('📋 Instructions:');
console.log('   • Long press Glyph Button to start timer');
console.log('   • Long press again to skip to next phase');
console.log('   • Work sessions: 25 minutes (●)');
console.log('   • Break sessions: 5 minutes (○)');
console.log('   • Timer loops automatically until switched off');
console.log('==================================');

const pomodoroTimer = createPomodoroTimer();

