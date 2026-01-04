import { defineStore } from "pinia";
import { api } from "@/services/api";

export const usePupilStore = defineStore("pupil", {
  state: () => ({
    presence: {
      loading: false,
      error: null,
      rate: null, // number | null
      from: null,
      to: null,
      schoolDays: null,
      pupilCount: null,
      absentSlots: null,
    },
  }),

  actions: {
    async fetchPresenceRate({ from = null, to = null } = {}) {
      if (this.presence.loading) return;

      this.presence.loading = true;
      this.presence.error = null;

      try {
        const { data } = await api.get("/attendance/presence-rate", {
          params: {
            ...(from ? { from } : {}),
            ...(to ? { to } : {}),
            _ts: Date.now(),
          },
        });

        this.presence.rate = typeof data?.rate === "number" ? data.rate : null;
        this.presence.from = data?.from ?? null;
        this.presence.to = data?.to ?? null;
        this.presence.schoolDays = data?.schoolDays ?? null;
        this.presence.pupilCount = data?.pupilCount ?? null;
        this.presence.absentSlots = data?.absentSlots ?? null;
      } catch (e) {
        this.presence.error = "Impossible de charger le taux de présence.";
        this.presence.rate = null;
        this.presence.from = null;
        this.presence.to = null;
      } finally {
        this.presence.loading = false;
      }
    },
  },
});