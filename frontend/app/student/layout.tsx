import AuthGuard from "@/components/AuthGuard";
import StudentSidebar from "@/components/student/StudentSidebar";
import StudentTopbar from "@/components/student/StudentTopbar";

export default function StudentLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthGuard allowedRoles={["Student"]}>
      <div className="min-h-screen flex bg-bg">
        <StudentSidebar />
        <div className="flex-1 flex flex-col min-w-0">
          <StudentTopbar />
          <main className="flex-1 p-6 max-w-5xl w-full mx-auto">{children}</main>
        </div>
      </div>
    </AuthGuard>
  );
}
