import { defineStore } from "pinia";
import { api } from "@/services/api";

export const usePupilStore = defineStore("pupil", {
  state: () => ({
    presence: {
      loading: false,
      error: null,
      rate: null,
      from: null,
      to: null,
      schoolDays: null,
      pupilCount: null,
      absentSlots: null,
    },

    currentClassroomId: null,
    pupils: [],

    loading: {
      pupils: false,
      addPupil: false,
    },

    error: {
      pupils: "",
      addPupil: "",
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

    async fetchPupilsForClassroom(classroomId) {
      this.currentClassroomId = classroomId || null;

      if (!classroomId) {
        this.pupils = [];
        return;
      }

      if (this.loading.pupils) return;

      this.loading.pupils = true;
      this.error.pupils = "";

      try {
        const { data } = await api.get(`/classrooms/${classroomId}/list/pupils`, { params: { _ts: Date.now() } });
        this.pupils = Array.isArray(data) ? data : [];
        return this.pupils;  
      } catch (e) {
        this.error.pupils = "Impossible de charger les élèves.";
        this.pupils = [];
        throw e;
      } finally {
        this.loading.pupils = false;
      }
    },

    async reloadPupilsForClassroom(classroomId) {
      return this.fetchPupilsForClassroom(classroomId);
    },

    async addPupilToClassroom(classroomId, pupilData) {
      if (!classroomId) throw new Error("Classroom ID manquant.");

      this.loading.addPupil = true;
      this.error.addPupil = "";

      try {
        const { data } = await api.post(`/classrooms/${classroomId}/pupils`, pupilData);
        if (data?.id) {
          this.pupils.unshift(data);
        } else {
          await this.fetchPupilsForClassroom(classroomId);
        }

        return data;
      } catch (e) {
        this.error.addPupil = "Impossible d'ajouter l'élève.";
        throw e;
      } finally {
        this.loading.addPupil = false;
      }
    },
},
})