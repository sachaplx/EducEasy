<template>
  <div class="skeleton-class-card">
    <div class="skeleton-accent"></div>

    <div class="skeleton-content">
      <div class="skeleton-header">
        <div class="skeleton-title"></div>
        <div class="skeleton-badge"></div>
      </div>

      <div class="skeleton-teacher-row">
        <div class="skeleton-avatar"></div>
        <div class="skeleton-teacher-info">
          <div class="skeleton-teacher-name"></div>
          <div class="skeleton-teacher-role"></div>
        </div>
      </div>

      <div class="skeleton-footer">
        <div class="skeleton-stat"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.skeleton-class-card {
  position: relative;
  background: white;
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.skeleton-accent {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #e0e0e0 0%, #d0d0d0 50%, #e0e0e0 100%);
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
}

.skeleton-content {
  padding: 20px;
}

.skeleton-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 12px;
}

.skeleton-title {
  flex: 1;
  height: 20px;
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

.skeleton-badge {
  width: 60px;
  height: 24px;
  background: linear-gradient(
    90deg,
    #f0f0f0 0%,
    #e0e0e0 20%,
    #f0f0f0 40%,
    #f0f0f0 100%
  );
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
  border-radius: 12px;
}

.skeleton-teacher-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.skeleton-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(
    90deg,
    #f0f0f0 0%,
    #e0e0e0 20%,
    #f0f0f0 40%,
    #f0f0f0 100%
  );
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
  flex-shrink: 0;
}

.skeleton-teacher-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.skeleton-teacher-name {
  width: 120px;
  height: 14px;
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

.skeleton-teacher-role {
  width: 80px;
  height: 12px;
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

.skeleton-footer {
  display: flex;
  gap: 12px;
}

.skeleton-stat {
  width: 100px;
  height: 14px;
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

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>
