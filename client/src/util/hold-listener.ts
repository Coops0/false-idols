export function registerHoldListener(
    showRole: () => void
) {
    let lastHoldStart = 0;

    document.addEventListener('pointerdown', () => {
        lastHoldStart = Date.now();
    }, { passive: true });

    document.addEventListener('pointerup', () => {
        const holdDuration = Date.now() - lastHoldStart;
        if (holdDuration >= 1300) {
            showRole();
        }
    }, { passive: true });
}