export const setupWakeLock = () => {
    document.addEventListener('click', go);

    let hasStarted = false;

    function go() {
        if (hasStarted) return;
        hasStarted = true;

        document.removeEventListener('click', go);
        requestAndMaintainWakeLock();
    }
};

const requestAndMaintainWakeLock = async () => {
    let wakeLock: WakeLockSentinel | null = null;

    const requestWakeLock = async () => {
        if (wakeLock?.released === true) {
            wakeLock = null;
        }

        if (wakeLock !== null) return;

        if ('WakeLock' in window && 'request' in window.WakeLock) {
            // @ts-expect-error - window.WakeLock isn't defined
            wakeLock = await window.WakeLock.request('screen');
        } else if ('wakeLock' in navigator && 'request' in navigator.wakeLock) {
            wakeLock = await navigator.wakeLock.request('screen');
        } else {
            return;
        }

        try {
            wakeLock?.addEventListener('release', () => (wakeLock = null));
        } catch {
            /* ignored */
        }
    };

    try {
        await requestWakeLock();
    } catch (err) {
        console.error('failed to request wake lock', err);
    }

    const requestIfNeeded = async () => {
        try {
            await requestWakeLock();
        } catch {
            /* ignored */
        }
    };

    setInterval(() => requestIfNeeded(), 2500);
    window.addEventListener('focus', requestIfNeeded);
    window.addEventListener('visibilitychange', () => {
        if (document.visibilityState === 'visible') {
            requestIfNeeded();
        }
    });
};