import { onMounted, onUnmounted, ref, useTemplateRef, watch } from 'vue';

export const useElementBounds = (element: ReturnType<typeof useTemplateRef<HTMLElement>>) => {
    const bounds = ref<DOMRect>({
        width: 0,
        height: 0,
        top: 0,
        bottom: 0,
        left: 0,
        right: 0,
        x: 0,
        y: 0,
        toJSON() {}
    });

    function updateElementBounds() {
        const el = element.value;
        if (!el) return;

        bounds.value = el.getBoundingClientRect();
    }

    let hasAttachedListeners = false;

    watch(element, el => {
        updateElementBounds();
        if (!el || hasAttachedListeners) return;

        hasAttachedListeners = true;
        el.addEventListener('resize', updateElementBounds);
        el.addEventListener('load', updateElementBounds);
    }, { immediate: true, deep: true });

    let updateInterval = -1;

    onMounted(() => {
        updateElementBounds();
        window.addEventListener('resize', updateElementBounds);

        updateInterval = setInterval(updateElementBounds, 2000);
    });

    onUnmounted(() => {
        window.removeEventListener('resize', updateElementBounds);
        if (element.value) {
            element.value.removeEventListener('resize', updateElementBounds);
            element.value.removeEventListener('load', updateElementBounds);
        }

        clearInterval(updateInterval);
    });

    return bounds;
};