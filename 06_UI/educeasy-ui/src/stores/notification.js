import { defineStore } from "pinia";

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [],
    loading: false,
  }),
  getters: {
    unreadCount: (state) => state.notifications.filter((n) => !n.read).length,
    sortedNotifications: (state) =>
      [...state.notifications].sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt),
      ),
  },
  actions: {
    // Mock data - à remplacer par un appel API
    async fetchNotifications() {
      this.loading = true;
      try {
        // Simuler un délai API
        await new Promise((resolve) => setTimeout(resolve, 300));

        // Mock notifications
        this.notifications = [
          {
            id: 1,
            type: "absence",
            title: "Absence à justifier",
            message: "Martin DUPONT absent ce matin",
            createdAt: new Date(Date.now() - 1000 * 60 * 30).toISOString(), // 30 min ago
            read: false,
            icon: "mdi-account-off",
            color: "warning",
          },
          {
            id: 2,
            type: "note",
            title: "Nouvelle note ajoutée",
            message: "Note en mathématiques pour Sophie MARTIN",
            createdAt: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(), // 2h ago
            read: false,
            icon: "mdi-note-text",
            color: "primary",
          },
          {
            id: 3,
            type: "alert",
            title: "Moyenne en baisse",
            message: "Paul BERNARD: moyenne < 10/20",
            createdAt: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString(), // 5h ago
            read: true,
            icon: "mdi-alert",
            color: "error",
          },
        ];
      } catch (error) {
        console.error("Error fetching notifications:", error);
      } finally {
        this.loading = false;
      }
    },

    markAsRead(notificationId) {
      const notification = this.notifications.find((n) => n.id === notificationId);
      if (notification) {
        notification.read = true;
      }
    },

    markAllAsRead() {
      this.notifications.forEach((n) => {
        n.read = true;
      });
    },

    removeNotification(notificationId) {
      const index = this.notifications.findIndex((n) => n.id === notificationId);
      if (index !== -1) {
        this.notifications.splice(index, 1);
      }
    },

    clearAll() {
      this.notifications = [];
    },
  },
});
