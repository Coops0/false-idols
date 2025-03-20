import { Role } from '@/game/messages.ts';
import type { IconVariant } from '@/components/ui/PlayerPreview.vue';

export const isNameValid = (name: string) => name.length >= 3 && name.length < 15 && /^[a-zA-Z0-9]+$/.test(name);
export const roleToVariant = (role: Role): IconVariant => {
    switch (role) {
        case Role.ANGEL:
            return 'angel';
        case Role.DEMON:
            return 'demon';
        case Role.SATAN:
            return 'satan';
    }
};