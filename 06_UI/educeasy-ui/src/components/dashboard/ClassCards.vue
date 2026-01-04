<template>
  <div
    class="cc group"
    :class="[variant.border, className]"
    role="button"
    tabindex="0"
    @click="$emit('click')"
    @keydown.enter="$emit('click')"
  >
    <!-- Decorative accent bar -->
    <div class="cc__accent" :class="variant.accent" />

    <div class="cc__inner">
      <!-- Header -->
      <div class="cc__header">
        <div class="cc__titleWrap">
          <h3 class="cc__title">
            {{ name }}
          </h3>

          <span class="cc__badge" :class="variant.badge">
            {{ level }}
          </span>
        </div>

        <v-icon class="cc__chevron" size="20">mdi-chevron-right</v-icon>
      </div>

      <!-- Info -->
      <div class="cc__info">
        <div class="cc__teacherRow">
          <v-avatar size="32" class="cc__avatar">
            <div class="cc__avatarFallback" :class="variant.badge">
              {{ teacherInitials }}
            </div>
          </v-avatar>

          <div class="cc__teacherText">
            <p class="cc__teacherName">{{ teacherName }}</p>
            <p class="cc__teacherRole">Enseignant(e)</p>
          </div>
        </div>

        <div class="cc__footer">
          <div class="cc__stat">
            <v-icon size="16" class="cc__statIcon">mdi-account-multiple</v-icon>
            <span>{{ studentCount }} élèves</span>
          </div>

          <div class="cc__stat">
            <v-icon size="16" class="cc__statIcon"
              >mdi-book-open-variant</v-icon
            >
            <span>{{ courseCount }} cours</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  name: { type: String, required: true },
  level: { type: String, required: true },
  teacherName: { type: String, required: true },
  teacherInitials: { type: String, required: true },
  studentCount: { type: Number, default: 0 },
  courseCount: { type: Number, default: 12 },

  // mêmes variantes que ton TSX
  color: {
    type: String,
    default: "blue",
    validator: (v) => ["blue", "purple", "orange", "green", "pink"].includes(v),
  },

  // équivalent className optionnel
  className: { type: String, default: "" },
});

defineEmits(["click"]);

const variant = computed(() => {
  const map = {
    blue: {
      border: "cc--blue",
      accent: "cc-accent--blue",
      badge: "cc-badge--blue",
    },
    purple: {
      border: "cc--purple",
      accent: "cc-accent--purple",
      badge: "cc-badge--purple",
    },
    orange: {
      border: "cc--orange",
      accent: "cc-accent--orange",
      badge: "cc-badge--orange",
    },
    green: {
      border: "cc--green",
      accent: "cc-accent--green",
      badge: "cc-badge--green",
    },
    pink: {
      border: "cc--pink",
      accent: "cc-accent--pink",
      badge: "cc-badge--pink",
    },
  };
  return map[props.color] || map.blue;
});
</script>

<style scoped>
/* === Reproduction fidèle de:
"group relative overflow-hidden rounded-xl bg-card border shadow-card p-5
 transition-all duration-300 hover:shadow-card-hover hover:-translate-y-0.5"
*/
.cc {
  position: relative;
  overflow: hidden;
  border-radius: 16px; /* rounded-xl */
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.92); /* bg-card + translucide (maquette) */
  backdrop-filter: blur(6px);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06); /* shadow-card */
  cursor: pointer;
  transition: transform 300ms ease, box-shadow 300ms ease,
    border-color 300ms ease;
}

.cc:hover {
  transform: translateY(-2px); /* hover:-translate-y-0.5 */
  box-shadow: 0 14px 26px rgba(0, 0, 0, 0.1); /* shadow-card-hover */
}

/* Accent bar "absolute left-0 top-0 h-full w-1 rounded-l-xl" */
.cc__accent {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  border-top-left-radius: 16px;
  border-bottom-left-radius: 16px;
}

/* Equivalent ml-2 space-y-4 */
.cc__inner {
  padding: 20px; /* p-5 */
  padding-left: 28px; /* ml-2 + place accent */
  display: flex;
  flex-direction: column;
  gap: 16px; /* space-y-4 */
}

/* Header */
.cc__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.cc__titleWrap {
  min-width: 0;
}

.cc__title {
  margin: 0;
  font-size: 18px; /* text-lg */
  font-weight: 600; /* font-semibold */
  color: rgba(0, 0, 0, 0.86);
  transition: color 200ms ease;
}

.cc:hover .cc__title {
  color: rgb(var(--v-theme-primary));
}

/* Badge (Badge variant secondary + colors.badge) */
.cc__badge {
  display: inline-flex;
  align-items: center;
  margin-top: 6px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

/* Chevron: "opacity-0 -translate-x-2 ... group-hover:opacity-100 group-hover:translate-x-0" */
.cc__chevron {
  opacity: 0;
  transform: translateX(-8px);
  transition: opacity 300ms ease, transform 300ms ease;
  color: rgba(0, 0, 0, 0.5);
}

.cc:hover .cc__chevron {
  opacity: 1;
  transform: translateX(0);
}

/* Info */
.cc__info {
  display: flex;
  flex-direction: column;
  gap: 12px; /* space-y-3 */
}

.cc__teacherRow {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cc__avatarFallback {
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  font-size: 12px;
  font-weight: 700;
}

.cc__teacherText {
  min-width: 0;
}

.cc__teacherName {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.82);
}

.cc__teacherRole {
  margin: 0;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
}

/* Footer: "flex items-center gap-4 pt-2 border-t border-border/50" */
.cc__footer {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 10px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
}

.cc__stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.55);
}

.cc__statIcon {
  color: rgba(0, 0, 0, 0.55);
}

/* === Color variants (fidèles à ton TSX) === */
/* blue: border-primary/20, accent bg-primary, badge bg-primary/10 text-primary */
.cc--blue {
  border-color: rgba(var(--v-theme-primary), 0.2);
}
.cc-accent--blue {
  background: rgb(var(--v-theme-primary));
}
.cc-badge--blue {
  background: rgba(var(--v-theme-primary), 0.1);
  color: rgb(var(--v-theme-primary));
}

/* purple: violet */
.cc--purple {
  border-color: rgba(124, 58, 237, 0.22);
}
.cc-accent--purple {
  background: rgb(124, 58, 237);
}
.cc-badge--purple {
  background: rgba(124, 58, 237, 0.14);
  color: rgb(88, 28, 135);
}

/* orange: accent */
.cc--orange {
  border-color: rgba(var(--v-theme-warning), 0.22);
}
.cc-accent--orange {
  background: rgb(var(--v-theme-warning));
}
.cc-badge--orange {
  background: rgba(var(--v-theme-warning), 0.12);
  color: rgb(var(--v-theme-warning));
}

/* green: success */
.cc--green {
  border-color: rgba(var(--v-theme-success), 0.22);
}
.cc-accent--green {
  background: rgb(var(--v-theme-success));
}
.cc-badge--green {
  background: rgba(var(--v-theme-success), 0.12);
  color: rgb(var(--v-theme-success));
}

/* pink */
.cc--pink {
  border-color: rgba(236, 72, 153, 0.22);
}
.cc-accent--pink {
  background: rgb(236, 72, 153);
}
.cc-badge--pink {
  background: rgba(236, 72, 153, 0.14);
  color: rgb(190, 24, 93);
}
</style>
