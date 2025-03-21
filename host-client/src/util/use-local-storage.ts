import { ref, watch } from 'vue';

export const useLocalStorage = <T>(key: string, defaultValue: T) => {
    const value = ref(defaultValue);

    try {
        const json = localStorage.getItem(key);
        if (json) {
            value.value = JSON.parse(json);
        }
    } catch {
        /* ignored */
    }

    watch(
        value,
        v => localStorage.setItem(key, JSON.stringify(v)),
        { immediate: true, deep: true }
    );

    return value;
};