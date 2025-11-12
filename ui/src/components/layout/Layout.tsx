import Box from '@mui/material/Box'
import Header from './Header'
import Sidebar from './Sidebar'
import Footer from './Footer'
import { PropsWithChildren } from 'react'

export default function Layout({ children }: PropsWithChildren) {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Header />
      <Box sx={{ display: 'flex', flex: 1, flexDirection: { xs: 'column', md: 'row' } }}>
        <Box sx={{ width: { md: 220, xs: '100%' } }} component="nav" aria-label="Primary">
          <Sidebar />
        </Box>
        <Box component="main" role="main" sx={{ flex: 1, p: 2 }}>
          {children}
        </Box>
      </Box>
      <Footer />
    </Box>
  )
}
