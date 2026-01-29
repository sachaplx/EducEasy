<template>
  <v-card rounded="xl" class="pa-4 skeleton-kpi" variant="flat">
    <div class="d-flex align-center justify-space-between">
      <div class="skeleton-text skeleton-title"></div>
      <div class="skeleton-avatar"></div>
    </div>

    <div class="skeleton-value mt-3"></div>
    <div class="skeleton-subtitle mt-2"></div>
  </v-card>
</template>

<style scoped>
.skeleton-kpi {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  overflow: hidden;
  position: relative;
}

.skeleton-text,
.skeleton-avatar,
.skeleton-value,
.skeleton-subtitle {
  background: linear-gradient(
    90deg,
    #f0f0f0 0%,
    #e0e0e0 20%,
    #f0f0f0 40%,
    #f0f0f0 100%
  );
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
  border-radius: 4px;
}

.skeleton-title {
  width: 60%;
  height: 14px;
}

.skeleton-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.skeleton-value {
  width: 50%;
  height: 28px;
  border-radius: 6px;
}

.skeleton-subtitle {
  width: 70%;
  height: 12px;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>
